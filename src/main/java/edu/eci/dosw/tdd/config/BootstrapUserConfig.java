package edu.eci.dosw.tdd.config;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.port.UserPersistencePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapUserConfig {
    @Bean
    public org.springframework.boot.CommandLineRunner defaultLibrarianRunner(
            UserPersistencePort users,
            PasswordEncoder passwordEncoder,
            @Value("${security.bootstrap.librarian.username:admin}") String username,
            @Value("${security.bootstrap.librarian.password:Admin123!}") String password,
            @Value("${security.bootstrap.librarian.name:Administrador}") String name) {
        return args -> {
            if (!users.existsByUsername(username)) {
                User librarian = new User();
                librarian.setName(name);
                librarian.setUsername(username);
                librarian.setPassword(passwordEncoder.encode(password));
                librarian.setRole(Role.LIBRARIAN);
                users.save(librarian);
            }
        };
    }
}
