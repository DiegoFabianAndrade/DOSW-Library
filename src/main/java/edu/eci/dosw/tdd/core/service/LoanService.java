package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    private final List<Loan> loans = new ArrayList<>();
    private final UserService userService;
    private final BookService bookService;

    public LoanService(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    public Loan createLoan(Integer userId, Integer bookId) {
        LoanValidator.validateLoanCreation(userId, bookId);
        userService.getUserById(userId);
        bookService.getBookById(bookId);

        long activeLoansForUser = loans.stream()
                .filter(loan -> loan.getUserId().equals(userId))
                .filter(loan -> loan.getStatus() == Status.ACTIVE)
                .count();
        if (activeLoansForUser >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new LoanLimitExceededException("El usuario " + userId + " excedio el limite de prestamos activos");
        }

        if (bookService.getAvailableCopies(bookId) <= 0) {
            throw new BookNotAvailableException("El libro " + bookId + " no tiene ejemplares disponibles");
        }

        bookService.decreaseStock(bookId);
        Loan loan = new Loan();
        loan.setId(IdGeneratorUtil.nextId());
        loan.setBookId(bookId);
        loan.setUserId(userId);
        loan.setLoanDate(DateUtil.today());
        loan.setStatus(Status.ACTIVE);
        loans.add(loan);
        return loan;
    }

    public Loan returnLoan(Integer loanId) {
        Loan loan = getLoanById(loanId);
        if (loan.getStatus() == Status.RETURNED) {
            throw new IllegalStateException("El prestamo " + loanId + " ya fue devuelto");
        }

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(DateUtil.today());
        bookService.increaseStock(loan.getBookId());
        return loan;
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public Loan getLoanById(Integer loanId) {
        return loans.stream()
                .filter(loan -> loan.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
    }
}
