package edu.eci.dosw.tdd.persistence.document;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    @Id
    private Integer id;

    private String name;
    private String username;
    private String password;
    private String role;
    private String email;
    private String membershipType;
    private LocalDateTime registeredAt;
}
