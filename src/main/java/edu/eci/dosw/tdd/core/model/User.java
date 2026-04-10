package edu.eci.dosw.tdd.core.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private String email;
    private MembershipType membershipType;
    private LocalDateTime registeredAt;
}