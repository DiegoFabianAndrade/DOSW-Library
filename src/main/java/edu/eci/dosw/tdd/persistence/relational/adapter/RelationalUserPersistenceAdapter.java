package edu.eci.dosw.tdd.persistence.relational.adapter;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.port.UserPersistencePort;
import edu.eci.dosw.tdd.persistence.relational.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("relational")
public class RelationalUserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;

    public RelationalUserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(userRepository.save(UserPersistenceMapper.toNewEntity(user)));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll().stream().map(UserPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
