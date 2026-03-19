package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final Map<Integer, Book> booksById = new HashMap<>();
    private final Map<Integer, Integer> stockByBookId = new HashMap<>();

    public Book addBook(Book book, Integer quantity) {
        BookValidator.validateBook(book);
        BookValidator.validateQuantity(quantity);

        Integer id = book.getId() != null ? book.getId() : IdGeneratorUtil.nextId();
        book.setId(id);
        book.setAvailable(quantity > 0);

        booksById.put(id, book);
        stockByBookId.put(id, quantity);
        return book;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(booksById.values());
    }

    public Book getBookById(Integer id) {
        Book book = booksById.get(id);
        if (book == null) {
            throw new BookNotFoundException("No se encontro un libro con id " + id);
        }
        return book;
    }

    public Book updateAvailability(Integer id, boolean available) {
        Book book = getBookById(id);
        book.setAvailable(available);
        return book;
    }

    public int getAvailableCopies(Integer bookId) {
        getBookById(bookId);
        return stockByBookId.getOrDefault(bookId, 0);
    }

    public void decreaseStock(Integer bookId) {
        int current = getAvailableCopies(bookId);
        if (current <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles para el libro " + bookId);
        }
        stockByBookId.put(bookId, current - 1);
        booksById.get(bookId).setAvailable(current - 1 > 0);
    }

    public void increaseStock(Integer bookId) {
        int current = getAvailableCopies(bookId);
        stockByBookId.put(bookId, current + 1);
        booksById.get(bookId).setAvailable(true);
    }
}
