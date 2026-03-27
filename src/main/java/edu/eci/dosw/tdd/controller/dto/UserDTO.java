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
@Schema(name = "UserDTO", description = "Usuario en la capa API")
public class UserDTO {
    @Schema(description = "Identificador del usuario", example = "1")
    private Integer id;
    @Schema(description = "Nombre completo", example = "Ana Perez")
    private String name;
    @Schema(description = "Nombre de usuario", example = "anaperez")
    private String username;
    @Schema(description = "Contrasena para autenticacion", example = "Password123!")
    private String password;
    @Schema(description = "Rol del usuario", example = "USER")
    private String role;
}
