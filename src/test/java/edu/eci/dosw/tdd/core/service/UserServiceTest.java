package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        UserService service = new UserService();
        User user = new User();
        user.setName("Diego");

        User created = service.registerUser(user);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void shouldFailWhenUserNotFound() {
        UserService service = new UserService();

        Assertions.assertThrows(UserNotFoundException.class, () -> service.getUserById(123));
    }
}
