package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.persistence.port.UserPersistencePort;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserPersistencePort users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserPersistencePort users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        UserValidator.validateUser(user);
        if (users.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return users.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return users.findAllUsers();
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return users
                .findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontro un usuario con id " + id));
    }
}
