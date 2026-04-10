package edu.eci.dosw.tdd.persistence.document.repository;

import edu.eci.dosw.tdd.persistence.document.UserDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, Integer> {

    Optional<UserDocument> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserDocument> findTopByOrderByIdDesc();
}
