package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.ErrorResponse;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.security.AppUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.Authentication;
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
        AppUserPrincipal principal = currentUser();
        if (principal.role() == Role.USER && !principal.id().equals(request.getUserId())) {
            throw new ForbiddenOperationException("Solo puede crear prestamos para su propio usuario");
        }
        Loan created = loanService.createLoan(request.getUserId(), request.getBookId());
        return LoanMapper.toDTO(created);
    }

    @GetMapping
    @Operation(summary = "Listar prestamos", description = "Devuelve el historial de prestamos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class))))
    public List<LoanDTO> getAllLoans(Authentication authentication) {
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        if (principal.role() == Role.USER) {
            return loanService.getLoansByUserId(principal.id()).stream().map(LoanMapper::toDTO).toList();
        }
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
            @PathVariable Integer id,
            Authentication authentication) {
        Loan loan = loanService.getLoanById(id);
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        if (principal.role() == Role.USER && !principal.id().equals(loan.getUserId())) {
            throw new ForbiddenOperationException("Solo puede consultar sus propios prestamos");
        }
        return LoanMapper.toDTO(loan);
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
            @PathVariable Integer loanId,
            Authentication authentication) {
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        if (principal.role() == Role.USER) {
            Loan loan = loanService.getLoanById(loanId);
            if (!principal.id().equals(loan.getUserId())) {
                throw new ForbiddenOperationException("Solo puede devolver sus propios prestamos");
            }
        }
        return LoanMapper.toDTO(loanService.returnLoan(loanId));
    }

    private AppUserPrincipal currentUser() {
        return (AppUserPrincipal) org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
