package edu.eci.dosw.tdd.persistence.document.repository;

import edu.eci.dosw.tdd.persistence.document.LoanDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanMongoRepository extends MongoRepository<LoanDocument, Integer> {

    List<LoanDocument> findByUserId(Integer userId);

    long countByUserIdAndStatus(Integer userId, String status);

    Optional<LoanDocument> findTopByOrderByIdDesc();
}
