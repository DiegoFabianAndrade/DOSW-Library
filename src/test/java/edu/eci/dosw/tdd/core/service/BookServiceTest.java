package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles({"test", "relational"})
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldAddBookSuccessfully() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");

        Book created = bookService.addBook(book, 2);

        Assertions.assertNotNull(created.getId());
        Assertions.assertTrue(created.isAvailable());
        Assertions.assertEquals(2, bookService.getAvailableCopies(created.getId()));
        Assertions.assertEquals(2, bookService.getTotalCopies(created.getId()));
        Assertions.assertEquals(1, bookRepository.count());
    }

    @Test
    void shouldFailWhenBookNotFound() {
        Assertions.assertThrows(BookNotFoundException.class, () -> bookService.getBookById(999));
    }
}
