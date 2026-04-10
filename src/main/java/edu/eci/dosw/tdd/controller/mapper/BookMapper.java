package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;

public class BookMapper {
    private BookMapper() {
    }

    public static Book toModel(BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setAvailable(dto.isAvailable());
        return book;
    }

    public static BookDTO toDTO(Book book, Integer availableCopies) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .available(book.isAvailable())
                .quantity(availableCopies)
                .totalCopies(book.getTotalCopies())
                .build();
    }
}
