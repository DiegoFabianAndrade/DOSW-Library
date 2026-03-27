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
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username del usuario es obligatorio");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contrasena del usuario es obligatoria");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio");
        }
    }
}
