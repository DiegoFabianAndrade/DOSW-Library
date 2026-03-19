package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.Book;

public class BookValidator {
    private BookValidator() {
    }

    public static void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (isBlank(book.getTitle())) {
            throw new IllegalArgumentException("El titulo del libro es obligatorio");
        }
        if (isBlank(book.getAuthor())) {
            throw new IllegalArgumentException("El autor del libro es obligatorio");
        }
    }

    public static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad de ejemplares debe ser mayor a 0");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
