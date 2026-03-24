package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;

public final class LoanPersistenceMapper {

    private LoanPersistenceMapper() {
    }

    public static Loan toDomain(LoanEntity entity) {
        Loan loan = new Loan();
        loan.setId(entity.getId());
        loan.setUserId(entity.getUser().getId());
        loan.setBookId(entity.getBook().getId());
        loan.setLoanDate(entity.getLoanDate());
        loan.setReturnDate(entity.getReturnDate());
        loan.setStatus(entity.getStatus());
        return loan;
    }
}
