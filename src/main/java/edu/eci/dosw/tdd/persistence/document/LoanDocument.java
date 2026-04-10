package edu.eci.dosw.tdd.persistence.document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDocument {

    @Id
    private Integer id;

    private Integer userId;
    private Integer bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String status;

    @Builder.Default
    private List<LoanAuditEntryDoc> history = new ArrayList<>();
}
