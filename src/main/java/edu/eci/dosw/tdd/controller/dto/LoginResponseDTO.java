package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "LoginResponseDTO", description = "Respuesta de autenticacion")
public class LoginResponseDTO {
    @Schema(description = "JWT de acceso")
    private String token;
    @Schema(example = "Bearer")
    private String type;
}
