package edu.eci.dosw.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "LoginRequestDTO", description = "Credenciales de autenticacion")
public class LoginRequestDTO {
    @Schema(example = "admin")
    private String username;
    @Schema(example = "Password123!")
    private String password;
}
