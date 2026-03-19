package edu.eci.dosw.tdd.controller.dto;

import edu.eci.dosw.tdd.core.model.Status;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Status status;
}
