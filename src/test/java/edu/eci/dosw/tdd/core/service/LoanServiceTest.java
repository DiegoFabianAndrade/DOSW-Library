package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateLoanSuccessfully() {
        User user = new User();
        user.setName("Ana");
        user.setUsername("ana1");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        User createdUser = userService.registerUser(user);

        Book book = new Book();
        book.setTitle("DDD");
        book.setAuthor("Eric Evans");
        Book createdBook = bookService.addBook(book, 1);

        Loan loan = loanService.createLoan(createdUser.getId(), createdBook.getId());

        Assertions.assertEquals(Status.ACTIVE, loan.getStatus());
        Assertions.assertEquals(0, bookService.getAvailableCopies(createdBook.getId()));
        Assertions.assertEquals(1, loanRepository.count());
    }

    @Test
    void shouldFailWhenBookHasNoStock() {
        User user = new User();
        user.setName("Ana");
        user.setUsername("ana2");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        User createdUser = userService.registerUser(user);

        Book book = new Book();
        book.setTitle("Refactoring");
        book.setAuthor("Martin Fowler");
        Book createdBook = bookService.addBook(book, 1);

        loanService.createLoan(createdUser.getId(), createdBook.getId());
        Assertions.assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan(createdUser.getId(), createdBook.getId()));
    }

    @Test
    void shouldFailWhenUserExceedsLoanLimit() {
        User user = new User();
        user.setName("Carlos");
        user.setUsername("carlos1");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        User createdUser = userService.registerUser(user);

        for (int i = 0; i < 4; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setAuthor("Author " + i);
            Book createdBook = bookService.addBook(book, 1);

            if (i < 3) {
                loanService.createLoan(createdUser.getId(), createdBook.getId());
            } else {
                Assertions.assertThrows(LoanLimitExceededException.class,
                        () -> loanService.createLoan(createdUser.getId(), createdBook.getId()));
            }
        }
    }

    @Test
    void shouldReturnLoanSuccessfully() {
        User user = new User();
        user.setName("Maria");
        user.setUsername("maria1");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        User createdUser = userService.registerUser(user);

        Book book = new Book();
        book.setTitle("Patterns");
        book.setAuthor("GoF");
        Book createdBook = bookService.addBook(book, 1);

        Loan loan = loanService.createLoan(createdUser.getId(), createdBook.getId());
        Loan returnedLoan = loanService.returnLoan(loan.getId());

        Assertions.assertEquals(Status.RETURNED, returnedLoan.getStatus());
        Assertions.assertEquals(1, bookService.getAvailableCopies(createdBook.getId()));
        Assertions.assertNotNull(returnedLoan.getReturnDate());
    }

    @Test
    void shouldGetLoanById() {
        User user = new User();
        user.setName("Luis");
        user.setUsername("luis1");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        User createdUser = userService.registerUser(user);

        Book book = new Book();
        book.setTitle("TDD");
        book.setAuthor("Kent Beck");
        Book createdBook = bookService.addBook(book, 1);

        Loan created = loanService.createLoan(createdUser.getId(), createdBook.getId());
        Loan found = loanService.getLoanById(created.getId());

        Assertions.assertEquals(created.getId(), found.getId());
        Assertions.assertEquals(created.getBookId(), found.getBookId());
    }

    @Test
    void shouldFailWhenLoanNotFound() {
        Assertions.assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById(99999));
    }
}
