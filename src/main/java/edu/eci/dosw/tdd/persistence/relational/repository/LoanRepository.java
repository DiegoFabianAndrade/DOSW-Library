package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, Integer> {

    long countByUser_IdAndStatus(Integer userId, Status status);

    @EntityGraph(attributePaths = {"user", "book"})
    @Override
    List<LoanEntity> findAll();

    @EntityGraph(attributePaths = {"user", "book"})
    @Override
    Optional<LoanEntity> findById(Integer id);

    @EntityGraph(attributePaths = {"user", "book"})
    List<LoanEntity> findByUser_Id(Integer userId);
}
