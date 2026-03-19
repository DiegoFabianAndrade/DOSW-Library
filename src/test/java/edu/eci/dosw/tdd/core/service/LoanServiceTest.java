package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoanServiceTest {

    @Test
    void shouldCreateLoanSuccessfully() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanService loanService = new LoanService(userService, bookService);

        User user = new User();
        user.setName("Ana");
        User createdUser = userService.registerUser(user);

        Book book = new Book();
        book.setTitle("DDD");
        book.setAuthor("Eric Evans");
        Book createdBook = bookService.addBook(book, 1);

        Loan loan = loanService.createLoan(createdUser.getId(), createdBook.getId());

        Assertions.assertEquals(Status.ACTIVE, loan.getStatus());
        Assertions.assertEquals(0, bookService.getAvailableCopies(createdBook.getId()));
    }

    @Test
    void shouldFailWhenBookHasNoStock() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanService loanService = new LoanService(userService, bookService);

        User user = new User();
        user.setName("Ana");
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
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanService loanService = new LoanService(userService, bookService);

        User user = new User();
        user.setName("Carlos");
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
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanService loanService = new LoanService(userService, bookService);

        User user = new User();
        user.setName("Maria");
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
}
