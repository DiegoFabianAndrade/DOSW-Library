package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book, Integer quantity) {
        BookValidator.validateBook(book);
        BookValidator.validateQuantity(quantity);
        BookEntity entity = BookPersistenceMapper.toNewEntity(book, quantity);
        BookEntity saved = bookRepository.save(entity);
        return BookPersistenceMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookPersistenceMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .map(BookPersistenceMapper::toDomain)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
    }

    public Book updateAvailability(Integer id, boolean available) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
        entity.setAvailable(available);
        return BookPersistenceMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public int getAvailableCopies(Integer bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        return entity.getAvailableCopies();
    }

    public void decreaseStock(Integer bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        int current = entity.getAvailableCopies();
        if (current <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles para el libro " + bookId);
        }
        entity.setAvailableCopies(current - 1);
        entity.setAvailable(current - 1 > 0);
        bookRepository.save(entity);
    }

    public void increaseStock(Integer bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        entity.setAvailableCopies(entity.getAvailableCopies() + 1);
        entity.setAvailable(true);
        bookRepository.save(entity);
    }
}
