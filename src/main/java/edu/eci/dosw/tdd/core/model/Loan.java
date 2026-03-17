package edu.eci.dosw.tdd.core.model;
import java.util.Date;
import lombok.Data;
@Data
public class Loan {
    private String book;
    private String user;
    private Integer loanDate;
    private Date status;
    private Date returnDate;
}