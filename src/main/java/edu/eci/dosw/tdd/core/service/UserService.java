package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        UserValidator.validateUser(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity saved = userRepository.save(UserPersistenceMapper.toNewEntity(user));
        return UserPersistenceMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserPersistenceMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("No se encontro un usuario con id " + id));
    }
}
