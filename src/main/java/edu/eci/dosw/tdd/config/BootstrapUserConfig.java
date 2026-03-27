package edu.eci.dosw.tdd.config;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapUserConfig {
    @Bean
    public org.springframework.boot.CommandLineRunner defaultLibrarianRunner(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${security.bootstrap.librarian.username:admin}") String username,
            @Value("${security.bootstrap.librarian.password:Admin123!}") String password,
            @Value("${security.bootstrap.librarian.name:Administrador}") String name) {
        return args -> {
            if (!userRepository.existsByUsername(username)) {
                userRepository.save(UserEntity.builder()
                        .name(name)
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .role(Role.LIBRARIAN)
                        .build());
            }
        };
    }
}
