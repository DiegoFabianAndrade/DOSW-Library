package edu.eci.dosw.tdd.core.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Loan {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Status status;
}