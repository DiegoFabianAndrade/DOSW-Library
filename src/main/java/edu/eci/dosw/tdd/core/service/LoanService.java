package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.port.LoanPersistencePort;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanService {
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    private final LoanPersistencePort loans;
    private final UserService userService;
    private final BookService bookService;

    public LoanService(LoanPersistencePort loans, UserService userService, BookService bookService) {
        this.loans = loans;
        this.userService = userService;
        this.bookService = bookService;
    }

    public Loan createLoan(Integer userId, Integer bookId) {
        LoanValidator.validateLoanCreation(userId, bookId);
        userService.getUserById(userId);
        bookService.getBookById(bookId);

        long activeLoansForUser = loans.countActiveLoansByUser(userId);
        if (activeLoansForUser >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new LoanLimitExceededException("El usuario " + userId + " excedio el limite de prestamos activos");
        }

        if (bookService.getAvailableCopies(bookId) <= 0) {
            throw new BookNotAvailableException("El libro " + bookId + " no tiene ejemplares disponibles");
        }

        bookService.decreaseStock(bookId);

        return loans.createLoan(userId, bookId, LocalDate.now(), Status.ACTIVE);
    }

    public Loan returnLoan(Integer loanId) {
        Loan loan = loans
                .findLoanById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
        if (loan.getStatus() == Status.RETURNED) {
            throw new IllegalStateException("El prestamo " + loanId + " ya fue devuelto");
        }

        bookService.increaseStock(loan.getBookId());
        return loans.markReturned(loanId, LocalDate.now());
    }

    public void deleteLoan(Integer loanId) {
        Loan loan = loans
                .findLoanById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
        if (loan.getStatus() == Status.ACTIVE) {
            bookService.increaseStock(loan.getBookId());
        }
        loans.deleteLoanById(loanId);
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loans.findAllLoans();
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(Integer loanId) {
        return loans
                .findLoanById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansByUserId(Integer userId) {
        return loans.findLoansByUserId(userId);
    }
}
