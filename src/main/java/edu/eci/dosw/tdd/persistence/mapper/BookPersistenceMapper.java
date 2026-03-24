package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;

public final class BookPersistenceMapper {

    private BookPersistenceMapper() {
    }

    public static Book toDomain(BookEntity entity) {
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setAvailable(entity.isAvailable());
        return book;
    }

    public static BookEntity toNewEntity(Book book, int initialCopies) {
        return BookEntity.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .availableCopies(initialCopies)
                .available(initialCopies > 0)
                .build();
    }
}
