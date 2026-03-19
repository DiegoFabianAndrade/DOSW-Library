package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BookServiceTest {

    @Test
    void shouldAddBookSuccessfully() {
        BookService service = new BookService();
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");

        Book created = service.addBook(book, 2);

        Assertions.assertNotNull(created.getId());
        Assertions.assertTrue(created.isAvailable());
        Assertions.assertEquals(2, service.getAvailableCopies(created.getId()));
    }

    @Test
    void shouldFailWhenBookNotFound() {
        BookService service = new BookService();

        Assertions.assertThrows(BookNotFoundException.class, () -> service.getBookById(999));
    }
}
