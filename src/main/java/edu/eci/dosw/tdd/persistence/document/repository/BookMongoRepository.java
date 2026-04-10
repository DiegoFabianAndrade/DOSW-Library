package edu.eci.dosw.tdd.persistence.document.repository;

import edu.eci.dosw.tdd.persistence.document.BookDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookMongoRepository extends MongoRepository<BookDocument, Integer> {

    Optional<BookDocument> findTopByOrderByIdDesc();
}
