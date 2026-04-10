package edu.eci.dosw.tdd.persistence.port;

import edu.eci.dosw.tdd.core.model.Book;
import java.util.List;

public interface BookPersistencePort {

    Book saveNew(Book book, int initialCopies);

    List<Book> findAllBooks();

    Book findBookById(Integer id);

    Book setAvailability(Integer id, boolean available);

    int getAvailableCopies(Integer bookId);

    int getTotalCopies(Integer bookId);

    void decreaseStock(Integer bookId);

    void increaseStock(Integer bookId);
}
