package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BookDTO", description = "Libro en la capa API")
public class BookDTO {
    @Schema(description = "Identificador del libro", example = "1")
    private Integer id;
    @Schema(description = "Titulo", example = "Clean Code")
    private String title;
    @Schema(description = "Autor", example = "Robert C. Martin")
    private String author;
    @Schema(description = "Indica si hay al menos un ejemplar disponible")
    private boolean available;
    @Schema(description = "Ejemplares disponibles para prestamo", example = "3")
    private Integer quantity;
    @Schema(description = "Total de ejemplares en catalogo (stock maximo)")
    private Integer totalCopies;
}
