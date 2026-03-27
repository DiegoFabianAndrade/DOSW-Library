package edu.eci.dosw.tdd.security;

import edu.eci.dosw.tdd.core.model.Role;

public record AppUserPrincipal(Integer id, String username, Role role) {
}
