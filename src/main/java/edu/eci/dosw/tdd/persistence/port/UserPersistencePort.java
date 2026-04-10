package edu.eci.dosw.tdd.persistence.port;

import edu.eci.dosw.tdd.core.model.User;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {

    User save(User user);

    List<User> findAllUsers();

    Optional<User> findUserById(Integer id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
