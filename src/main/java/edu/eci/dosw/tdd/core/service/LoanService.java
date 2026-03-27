package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanService {
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookService bookService;

    public LoanService(
            LoanRepository loanRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            UserService userService,
            BookService bookService) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public Loan createLoan(Integer userId, Integer bookId) {
        LoanValidator.validateLoanCreation(userId, bookId);
        userService.getUserById(userId);
        bookService.getBookById(bookId);

        long activeLoansForUser = loanRepository.countByUser_IdAndStatus(userId, Status.ACTIVE);
        if (activeLoansForUser >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new LoanLimitExceededException("El usuario " + userId + " excedio el limite de prestamos activos");
        }

        if (bookService.getAvailableCopies(bookId) <= 0) {
            throw new BookNotAvailableException("El libro " + bookId + " no tiene ejemplares disponibles");
        }

        bookService.decreaseStock(bookId);

        LoanEntity entity = LoanEntity.builder()
                .user(userRepository.getReferenceById(userId))
                .book(bookRepository.getReferenceById(bookId))
                .loanDate(LocalDate.now())
                .status(Status.ACTIVE)
                .build();
        LoanEntity saved = loanRepository.save(entity);
        return LoanPersistenceMapper.toDomain(saved);
    }

    public Loan returnLoan(Integer loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
        if (loan.getStatus() == Status.RETURNED) {
            throw new IllegalStateException("El prestamo " + loanId + " ya fue devuelto");
        }

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(LocalDate.now());
        bookService.increaseStock(loan.getBook().getId());
        return LoanPersistenceMapper.toDomain(loanRepository.save(loan));
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(LoanPersistenceMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(Integer loanId) {
        return loanRepository.findById(loanId)
                .map(LoanPersistenceMapper::toDomain)
                .orElseThrow(() -> new LoanNotFoundException("No se encontro un prestamo con id " + loanId));
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansByUserId(Integer userId) {
        return loanRepository.findByUser_Id(userId).stream()
                .map(LoanPersistenceMapper::toDomain)
                .toList();
    }
}
