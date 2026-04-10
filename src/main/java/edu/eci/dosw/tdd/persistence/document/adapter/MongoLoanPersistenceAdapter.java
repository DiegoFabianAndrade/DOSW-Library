package edu.eci.dosw.tdd.persistence.document.adapter;

import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanAuditEvent;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.document.LoanAuditEntryDoc;
import edu.eci.dosw.tdd.persistence.document.LoanDocument;
import edu.eci.dosw.tdd.persistence.document.repository.LoanMongoRepository;
import edu.eci.dosw.tdd.persistence.port.LoanPersistencePort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mongo")
public class MongoLoanPersistenceAdapter implements LoanPersistencePort {

    private final LoanMongoRepository loanMongoRepository;

    public MongoLoanPersistenceAdapter(LoanMongoRepository loanMongoRepository) {
        this.loanMongoRepository = loanMongoRepository;
    }

    @Override
    public Loan createLoan(Integer userId, Integer bookId, LocalDate loanDate, Status status) {
        int id = nextId();
        List<LoanAuditEntryDoc> history = new ArrayList<>();
        history.add(LoanAuditEntryDoc.builder()
                .status(status.name())
                .at(LocalDateTime.now())
                .build());
        LoanDocument doc = LoanDocument.builder()
                .id(id)
                .userId(userId)
                .bookId(bookId)
                .loanDate(loanDate)
                .status(status.name())
                .history(history)
                .build();
        return toDomain(loanMongoRepository.save(doc));
    }

    @Override
    public Optional<Loan> findLoanById(Integer loanId) {
        return loanMongoRepository.findById(loanId).map(this::toDomain);
    }

    @Override
    public List<Loan> findAllLoans() {
        return loanMongoRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Loan> findLoansByUserId(Integer userId) {
        return loanMongoRepository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public long countActiveLoansByUser(Integer userId) {
        return loanMongoRepository.countByUserIdAndStatus(userId, Status.ACTIVE.name());
    }

    @Override
    public Loan markReturned(Integer loanId, LocalDate returnDate) {
        LoanDocument doc = loanMongoRepository
                .findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
        doc.setStatus(Status.RETURNED.name());
        doc.setReturnDate(returnDate);
        if (doc.getHistory() == null) {
            doc.setHistory(new ArrayList<>());
        }
        doc.getHistory()
                .add(LoanAuditEntryDoc.builder()
                        .status(Status.RETURNED.name())
                        .at(LocalDateTime.now())
                        .build());
        return toDomain(loanMongoRepository.save(doc));
    }

    @Override
    public void deleteLoanById(Integer loanId) {
        if (!loanMongoRepository.existsById(loanId)) {
            throw new LoanNotFoundException("No se encontro un prestamo con id " + loanId);
        }
        loanMongoRepository.deleteById(loanId);
    }

    private int nextId() {
        return loanMongoRepository.findTopByOrderByIdDesc().map(LoanDocument::getId).map(i -> i + 1).orElse(1);
    }

    private Loan toDomain(LoanDocument doc) {
        Loan loan = new Loan();
        loan.setId(doc.getId());
        loan.setUserId(doc.getUserId());
        loan.setBookId(doc.getBookId());
        loan.setLoanDate(doc.getLoanDate());
        loan.setReturnDate(doc.getReturnDate());
        loan.setStatus(Status.valueOf(doc.getStatus()));
        if (doc.getHistory() != null) {
            loan.setHistory(
                    doc.getHistory().stream()
                            .map(e -> new LoanAuditEvent(Status.valueOf(e.getStatus()), e.getAt()))
                            .toList());
        }
        return loan;
    }
}
