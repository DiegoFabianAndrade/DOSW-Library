package edu.eci.dosw.tdd.persistence.relational.adapter;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.port.BookPersistencePort;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("relational")
public class RelationalBookPersistenceAdapter implements BookPersistencePort {

    private final BookRepository bookRepository;

    public RelationalBookPersistenceAdapter(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book saveNew(Book book, int initialCopies) {
        BookEntity saved = bookRepository.save(BookPersistenceMapper.toNewEntity(book, initialCopies));
        return BookPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll().stream().map(BookPersistenceMapper::toDomain).toList();
    }

    @Override
    public Book findBookById(Integer id) {
        return bookRepository
                .findById(id)
                .map(BookPersistenceMapper::toDomain)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
    }

    @Override
    public Book setAvailability(Integer id, boolean available) {
        BookEntity entity = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + id));
        entity.setAvailable(available);
        return BookPersistenceMapper.toDomain(bookRepository.save(entity));
    }

    @Override
    public int getAvailableCopies(Integer bookId) {
        BookEntity entity = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        return entity.getAvailableCopies();
    }

    @Override
    public int getTotalCopies(Integer bookId) {
        BookEntity entity = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        return entity.getTotalCopies();
    }

    @Override
    public void decreaseStock(Integer bookId) {
        BookEntity entity = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        int current = entity.getAvailableCopies();
        if (current <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles para el libro " + bookId);
        }
        entity.setAvailableCopies(current - 1);
        entity.setAvailable(current - 1 > 0);
        bookRepository.save(entity);
    }

    @Override
    public void increaseStock(Integer bookId) {
        BookEntity entity = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No se encontro un libro con id " + bookId));
        int max = entity.getTotalCopies();
        int next = entity.getAvailableCopies() + 1;
        if (next > max) {
            throw new IllegalStateException(
                    "Las copias disponibles no pueden superar el stock total (" + max + ") del libro " + bookId);
        }
        entity.setAvailableCopies(next);
        entity.setAvailable(next > 0);
        bookRepository.save(entity);
    }
}
