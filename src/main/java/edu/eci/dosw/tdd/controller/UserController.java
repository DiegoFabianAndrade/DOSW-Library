package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.ErrorResponse;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Registro y consulta de usuarios")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Registrar usuario", description = "Crea un usuario en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public UserDTO registerUser(@RequestBody UserDTO userDTO) {
        User created = userService.registerUser(UserMapper.toModel(userDTO));
        return UserMapper.toDTO(created);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(UserMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id", description = "Busca un usuario por su identificacion.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public UserDTO getUserById(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Integer id) {
        return UserMapper.toDTO(userService.getUserById(id));
    }
}
