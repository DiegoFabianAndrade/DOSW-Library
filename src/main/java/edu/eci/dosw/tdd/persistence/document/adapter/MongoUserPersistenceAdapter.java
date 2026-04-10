package edu.eci.dosw.tdd.persistence.document.adapter;

import edu.eci.dosw.tdd.core.model.MembershipType;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.document.UserDocument;
import edu.eci.dosw.tdd.persistence.document.repository.UserMongoRepository;
import edu.eci.dosw.tdd.persistence.port.UserPersistencePort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mongo")
public class MongoUserPersistenceAdapter implements UserPersistencePort {

    private final UserMongoRepository userMongoRepository;

    public MongoUserPersistenceAdapter(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public User save(User user) {
        UserDocument doc = toDocument(user);
        if (doc.getId() == null) {
            doc.setId(nextId());
        }
        if (doc.getRegisteredAt() == null) {
            doc.setRegisteredAt(LocalDateTime.now());
        }
        return toDomain(userMongoRepository.save(doc));
    }

    @Override
    public List<User> findAllUsers() {
        return userMongoRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userMongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMongoRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMongoRepository.existsByUsername(username);
    }

    private int nextId() {
        return userMongoRepository.findTopByOrderByIdDesc().map(UserDocument::getId).map(i -> i + 1).orElse(1);
    }

    private UserDocument toDocument(User user) {
        UserDocument.UserDocumentBuilder b = UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .email(user.getEmail());
        if (user.getMembershipType() != null) {
            b.membershipType(user.getMembershipType().name());
        }
        b.registeredAt(user.getRegisteredAt());
        return b.build();
    }

    private User toDomain(UserDocument doc) {
        User user = new User();
        user.setId(doc.getId());
        user.setName(doc.getName());
        user.setUsername(doc.getUsername());
        user.setPassword(doc.getPassword());
        if (doc.getRole() != null) {
            user.setRole(Role.valueOf(doc.getRole()));
        }
        user.setEmail(doc.getEmail());
        if (doc.getMembershipType() != null && !doc.getMembershipType().isBlank()) {
            user.setMembershipType(MembershipType.valueOf(doc.getMembershipType()));
        }
        user.setRegisteredAt(doc.getRegisteredAt());
        return user;
    }
}
