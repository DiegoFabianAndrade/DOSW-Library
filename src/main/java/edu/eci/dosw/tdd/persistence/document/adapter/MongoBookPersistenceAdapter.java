package edu.eci.dosw.tdd.persistence.document.adapter;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.document.BookDocument;
import edu.eci.dosw.tdd.persistence.document.repository.BookMongoRepository;
import edu.eci.dosw.tdd.persistence.port.BookPersistencePort;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mongo")
public class MongoBookPersistenceAdapter implements BookPersistencePort {

    private final BookMongoRepository bookMongoRepository;

    public MongoBookPersistenceAdapter(BookMongoRepository bookMongoRepository) {
        this.bookMongoRepository = bookMongoRepository;
    }

    @Override
    public Book saveNew(Book book, int initialCopies) {
        int id = nextId();
        BookDocument doc = BookDocument.builder()
                .id(id)
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(initialCopies)
                .availableCopies(initialCopies)
                .available(initialCopies > 0)
                .categories(Collections.emptyList())
                .publicationType("LIBRO_IMPRESO")
                .catalogAddedAt(LocalDateTime.now())
                .build();
        return toDomain(bookMongoRepository.save(doc));
    }

    @Override
    public List<Book> findAllBooks() {
        return bookMongoRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Book findBookById(Integer id) {
        return bookMongoRepository
                .findById(id)
                .map(this::toDomain)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
    }

    @Override
    public Book setAvailability(Integer id, boolean available) {
        BookDocument doc = bookMongoRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
        doc.setAvailable(available);
        return toDomain(bookMongoRepository.save(doc));
    }

    @Override
    public int getAvailableCopies(Integer bookId) {
        BookDocument doc = bookMongoRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        return doc.getAvailableCopies();
    }

    @Override
    public int getTotalCopies(Integer bookId) {
        BookDocument doc = bookMongoRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        return doc.getTotalCopies();
    }

    @Override
    public void decreaseStock(Integer bookId) {
        BookDocument doc = bookMongoRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        int current = doc.getAvailableCopies();
        if (current <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles para el libro " + bookId);
        }
        doc.setAvailableCopies(current - 1);
        doc.setAvailable(current - 1 > 0);
        bookMongoRepository.save(doc);
    }

    @Override
    public void increaseStock(Integer bookId) {
        BookDocument doc = bookMongoRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        int max = doc.getTotalCopies();
        int next = doc.getAvailableCopies() + 1;
        if (next > max) {
            throw new IllegalStateException(
                    "Las copias disponibles no pueden superar el stock total (" + max + ") del libro " + bookId);
        }
        doc.setAvailableCopies(next);
        doc.setAvailable(next > 0);
        bookMongoRepository.save(doc);
    }

    private int nextId() {
        return bookMongoRepository.findTopByOrderByIdDesc().map(BookDocument::getId).map(i -> i + 1).orElse(1);
    }

    private Book toDomain(BookDocument doc) {
        Book book = new Book();
        book.setId(doc.getId());
        book.setTitle(doc.getTitle());
        book.setAuthor(doc.getAuthor());
        book.setAvailable(doc.isAvailable());
        book.setTotalCopies(doc.getTotalCopies());
        book.setAvailableCopies(doc.getAvailableCopies());
        return book;
    }
}
