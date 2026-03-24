package edu.eci.dosw.tdd.controller.dto;

import edu.eci.dosw.tdd.core.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanDTO", description = "Prestamo en la capa API")
public class LoanDTO {
    @Schema(description = "Identificador del prestamo", example = "1")
    private Integer id;
    @Schema(description = "Identificador del libro", example = "2")
    private Integer bookId;
    @Schema(description = "Identificador del usuario", example = "1")
    private Integer userId;
    @Schema(description = "Fecha de prestamo")
    private LocalDate loanDate;
    @Schema(description = "Fecha de devolucion")
    private LocalDate returnDate;
    @Schema(description = "Estado del prestamo")
    private Status status;
}
