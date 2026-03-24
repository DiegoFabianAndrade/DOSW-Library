package edu.eci.dosw.tdd.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.http.MediaType;
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

    @BeforeEach
    void clean() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void postBook_insertsRowInDatabase() throws Exception {
        String body = "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"quantity\":4}";
        MvcResult result = mockMvc.perform(post("/api/books")
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
    void getAllBooks_readsFromDatabase() throws Exception {
        bookRepository.save(BookEntity.builder()
                .title("Libro A")
                .author("Autor A")
                .availableCopies(2)
                .available(true)
                .build());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Libro A"));
    }

    @Test
    void getBookById_readsFromDatabase() throws Exception {
        BookEntity saved = bookRepository.save(BookEntity.builder()
                .title("Solo")
                .author("Uno")
                .availableCopies(1)
                .available(true)
                .build());

        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Uno"));
    }

    @Test
    void patchAvailability_updatesDatabase() throws Exception {
        BookEntity saved = bookRepository.save(BookEntity.builder()
                .title("X")
                .author("Y")
                .availableCopies(1)
                .available(true)
                .build());

        mockMvc.perform(patch("/api/books/" + saved.getId() + "/availability")
                        .param("available", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        assertThat(bookRepository.findById(saved.getId()).orElseThrow().isAvailable()).isFalse();
    }

    @Test
    void postUser_insertsRowInDatabase() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pedro Lopez\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro Lopez"))
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findById(id)).isPresent();
    }

    @Test
    void getUsersAndById_readFromDatabase() throws Exception {
        userRepository.save(UserEntity.builder().name("Ana").build());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana"));

        Integer id = userRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    void postLoan_persistsLoanAndDecrementsCopies() throws Exception {
        UserEntity u = userRepository.save(UserEntity.builder().name("U1").build());
        BookEntity b = bookRepository.save(BookEntity.builder()
                .title("B1")
                .author("A1")
                .availableCopies(2)
                .available(true)
                .build());

        String loanBody = String.format("{\"userId\":%d,\"bookId\":%d}", u.getId(), b.getId());
        MvcResult loanResult = mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        Integer loanId = JsonPath.read(loanResult.getResponse().getContentAsString(), "$.id");
        assertThat(loanRepository.count()).isEqualTo(1);
        assertThat(bookRepository.findById(b.getId()).orElseThrow().getAvailableCopies()).isEqualTo(1);

        LoanEntity row = loanRepository.findById(loanId).orElseThrow();
        assertThat(row.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void getLoansAndById_readFromDatabase() throws Exception {
        UserEntity u = userRepository.save(UserEntity.builder().name("U2").build());
        BookEntity b = bookRepository.save(BookEntity.builder()
                .title("B2")
                .author("A2")
                .availableCopies(1)
                .available(true)
                .build());
        loanRepository.save(LoanEntity.builder()
                .user(u)
                .book(b)
                .loanDate(java.time.LocalDate.now())
                .status(Status.ACTIVE)
                .build());

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(b.getId()));

        Integer loanId = loanRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/loans/" + loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(u.getId()));
    }

    @Test
    void postReturn_updatesLoanAndRestoresCopies() throws Exception {
        UserEntity u = userRepository.save(UserEntity.builder().name("U3").build());
        BookEntity b = bookRepository.save(BookEntity.builder()
                .title("B3")
                .author("A3")
                .availableCopies(1)
                .available(true)
                .build());
        LoanEntity loan = loanRepository.save(LoanEntity.builder()
                .user(u)
                .book(b)
                .loanDate(java.time.LocalDate.now())
                .status(Status.ACTIVE)
                .build());
        b.setAvailableCopies(0);
        b.setAvailable(false);
        bookRepository.save(b);

        mockMvc.perform(post("/api/loans/" + loan.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        LoanEntity updated = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(Status.RETURNED);
        assertThat(updated.getReturnDate()).isNotNull();
        assertThat(bookRepository.findById(b.getId()).orElseThrow().getAvailableCopies()).isEqualTo(1);
    }
}
