package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.ErrorResponse;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Prestamos", description = "Creacion, devolucion y consulta de prestamos")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @Operation(summary = "Crear prestamo", description = "Asocia un libro prestado a un usuario si hay stock y reglas de negocio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamo creado",
                    content = @Content(schema = @Schema(implementation = LoanDTO.class))),
            @ApiResponse(responseCode = "400", description = "No se puede prestar",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Libro o usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LoanDTO createLoan(@RequestBody LoanDTO request) {
        Loan created = loanService.createLoan(request.getUserId(), request.getBookId());
        return LoanMapper.toDTO(created);
    }

    @GetMapping
    @Operation(summary = "Listar prestamos", description = "Devuelve el historial de prestamos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class))))
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans().stream().map(LoanMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener prestamo por id", description = "Consulta un prestamo por su identificacion.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamo encontrado",
                    content = @Content(schema = @Schema(implementation = LoanDTO.class))),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LoanDTO getLoanById(
            @Parameter(description = "Identificador del prestamo", example = "1")
            @PathVariable Integer id) {
        return LoanMapper.toDTO(loanService.getLoanById(id));
    }

    @PostMapping("/{loanId}/return")
    @Operation(summary = "Devolver prestamo", description = "Marca el prestamo como devuelto y repone stock del libro.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devolucion registrada",
                    content = @Content(schema = @Schema(implementation = LoanDTO.class))),
            @ApiResponse(responseCode = "400", description = "Estado invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LoanDTO returnLoan(
            @Parameter(description = "Identificador del prestamo", example = "1")
            @PathVariable Integer loanId) {
        return LoanMapper.toDTO(loanService.returnLoan(loanId));
    }
}
