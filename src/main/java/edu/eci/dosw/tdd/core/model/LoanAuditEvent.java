package edu.eci.dosw.tdd.core.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanAuditEvent {
    private Status status;
    private LocalDateTime at;
}
