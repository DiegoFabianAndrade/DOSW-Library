package edu.eci.dosw.tdd.persistence.port;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanPersistencePort {

    Loan createLoan(Integer userId, Integer bookId, LocalDate loanDate, Status status);

    Optional<Loan> findLoanById(Integer loanId);

    List<Loan> findAllLoans();

    List<Loan> findLoansByUserId(Integer userId);

    long countActiveLoansByUser(Integer userId);

    Loan markReturned(Integer loanId, LocalDate returnDate);

    void deleteLoanById(Integer loanId);
}
