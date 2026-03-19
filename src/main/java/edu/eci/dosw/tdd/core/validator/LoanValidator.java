package edu.eci.dosw.tdd.core.validator;

public class LoanValidator {
    private LoanValidator() {
    }

    public static void validateLoanCreation(Integer userId, Integer bookId) {
        if (userId == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio");
        }
        if (bookId == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio");
        }
    }
}
