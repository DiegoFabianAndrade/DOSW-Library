package edu.eci.dosw.tdd.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class LibraryApiFunctionalIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("dosw_library")
            .withUsername("dosw")
            .withPassword("dosw");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("springdoc.api-docs.enabled", () -> "false");
        registry.add("springdoc.swagger-ui.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        createUser("Bibliotecario", "librarian", "Password123!", Role.LIBRARIAN);
        createUser("Usuario", "user", "Password123!", Role.USER);
    }

    @Test
    void accessWithoutToken_mustBeRejected() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessWithInvalidToken_mustBeRejected() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotExecuteAdministrativeOperation() throws Exception {
        String userToken = login("user", "Password123!");
        String body = "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"quantity\":4}";
        mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void librarianCanCreateBooks() throws Exception {
        String librarianToken = login("librarian", "Password123!");
        String body = "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"quantity\":4}";
        MvcResult result = mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        assertThat(bookRepository.count()).isEqualTo(1);
        BookEntity stored = bookRepository.findById(id).orElseThrow();
        assertThat(stored.getAvailableCopies()).isEqualTo(4);
        assertThat(stored.isAvailable()).isTrue();
    }

    @Test
    void userCanCreateAndConsultOwnLoan() throws Exception {
        String librarianToken = login("librarian", "Password123!");
        String userToken = login("user", "Password123!");

        bookRepository.save(BookEntity.builder()
                .title("Libro A")
                .author("Autor A")
                .availableCopies(2)
                .available(true)
                .build());

        Integer userId = userRepository.findByUsername("user").orElseThrow().getId();
        Integer bookId = bookRepository.findAll().get(0).getId();
        String loanBody = String.format("{\"userId\":%d,\"bookId\":%d}", userId, bookId);

        MvcResult loanResult = mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanBody))
                .andExpect(status().isOk())
                .andReturn();
        Integer loanId = JsonPath.read(loanResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/loans/" + loanId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));

        mockMvc.perform(get("/api/loans/" + loanId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken))
                .andExpect(status().isOk());
    }

    @Test
    void userCannotReadAnotherUsersLoan() throws Exception {
        String userToken = login("user", "Password123!");
        UserEntity otherUser = createUser("Otro", "otheruser", "Password123!", Role.USER);

        BookEntity b = bookRepository.save(BookEntity.builder()
                .title("B4")
                .author("A4")
                .availableCopies(1)
                .available(true)
                .build());
        LoanEntity loan = loanRepository.save(LoanEntity.builder()
                .user(otherUser)
                .book(b)
                .loanDate(java.time.LocalDate.now())
                .status(Status.ACTIVE)
                .build());

        mockMvc.perform(get("/api/loans/" + loan.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private UserEntity createUser(String name, String username, String rawPassword, Role role) {
        return userRepository.save(UserEntity.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build());
    }

    private String login(String username, String password) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}
