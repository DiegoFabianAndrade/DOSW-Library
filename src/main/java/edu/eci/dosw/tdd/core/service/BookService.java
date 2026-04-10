package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.persistence.port.BookPersistencePort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {
    private final BookPersistencePort books;

    public BookService(BookPersistencePort books) {
        this.books = books;
    }

    public Book addBook(Book book, Integer quantity) {
        BookValidator.validateBook(book);
        BookValidator.validateQuantity(quantity);
        return books.saveNew(book, quantity);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return books.findAllBooks();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Integer id) {
        return books.findBookById(id);
    }

    public Book updateAvailability(Integer id, boolean available) {
        return books.setAvailability(id, available);
    }

    @Transactional(readOnly = true)
    public int getAvailableCopies(Integer bookId) {
        return books.getAvailableCopies(bookId);
    }

    @Transactional(readOnly = true)
    public int getTotalCopies(Integer bookId) {
        return books.getTotalCopies(bookId);
    }

    public void decreaseStock(Integer bookId) {
        books.decreaseStock(bookId);
    }

    public void increaseStock(Integer bookId) {
        books.increaseStock(bookId);
    }
}
