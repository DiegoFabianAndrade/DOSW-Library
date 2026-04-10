package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.controller.dto.LoginRequestDTO;
import edu.eci.dosw.tdd.controller.dto.LoginResponseDTO;
import edu.eci.dosw.tdd.core.exception.UnauthorizedException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.port.UserPersistencePort;
import edu.eci.dosw.tdd.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserPersistencePort users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserPersistencePort users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new UnauthorizedException("Credenciales invalidas");
        }
        User user = users
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .build();
    }
}
