package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.User;

public class UserValidator {
    private UserValidator() {
    }

    public static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
    }
}
