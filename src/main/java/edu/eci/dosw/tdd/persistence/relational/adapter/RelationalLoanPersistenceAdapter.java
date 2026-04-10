package edu.eci.dosw.tdd.persistence.relational.adapter;

import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.port.LoanPersistencePort;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("relational")
public class RelationalLoanPersistenceAdapter implements LoanPersistencePort {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public RelationalLoanPersistenceAdapter(
            LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Loan createLoan(Integer userId, Integer bookId, LocalDate loanDate, Status status) {
        LoanEntity entity = LoanEntity.builder()
                .user(userRepository.getReferenceById(userId))
                .book(bookRepository.getReferenceById(bookId))
                .loanDate(loanDate)
                .status(status)
                .build();
        return LoanPersistenceMapper.toDomain(loanRepository.save(entity));
    }

    @Override
    public Optional<Loan> findLoanById(Integer loanId) {
        return loanRepository.findById(loanId).map(LoanPersistenceMapper::toDomain);
    }

    @Override
    public List<Loan> findAllLoans() {
        return loanRepository.findAll().stream().map(LoanPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findLoansByUserId(Integer userId) {
        return loanRepository.findByUser_Id(userId).stream().map(LoanPersistenceMapper::toDomain).toList();
    }

    @Override
    public long countActiveLoansByUser(Integer userId) {
        return loanRepository.countByUser_IdAndStatus(userId, Status.ACTIVE);
    }

    @Override
    public Loan markReturned(Integer loanId, LocalDate returnDate) {
        LoanEntity loan = loanRepository
                .findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(returnDate);
        return LoanPersistenceMapper.toDomain(loanRepository.save(loan));
    }

    @Override
    public void deleteLoanById(Integer loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new LoanNotFoundException("No se encontro un prestamo con id " + loanId);
        }
        loanRepository.deleteById(loanId);
    }
}
