package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = new User();
        user.setName("Diego");

        User created = userService.registerUser(user);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(1, userService.getAllUsers().size());
        Assertions.assertEquals(1, userRepository.count());
    }

    @Test
    void shouldFailWhenUserNotFound() {
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(123));
    }
}
