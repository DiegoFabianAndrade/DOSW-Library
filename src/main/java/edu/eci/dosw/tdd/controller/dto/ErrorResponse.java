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
@Schema(name = "ErrorResponse", description = "Cuerpo estandar para errores HTTP")
public class ErrorResponse {
    @Schema(example = "2026-03-24T12:00:00")
    private String timestamp;
    @Schema(example = "404")
    private int status;
    @Schema(example = "Not Found")
    private String error;
    @Schema(example = "No se encontro un libro con id 99")
    private String message;
}
