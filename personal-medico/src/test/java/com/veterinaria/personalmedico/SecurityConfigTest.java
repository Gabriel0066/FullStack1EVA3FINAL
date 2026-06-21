package com.veterinaria.personalmedico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para SecurityConfig")
class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    @DisplayName("passwordEncoder debe retornar BCryptPasswordEncoder")
    void passwordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    @DisplayName("userDetailsService debe crear usuario admin con rol USER")
    void userDetailsService() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        InMemoryUserDetailsManager manager = config.userDetailsService(encoder);
        assertNotNull(manager);

        UserDetails admin = manager.loadUserByUsername("admin");
        assertEquals("admin", admin.getUsername());
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
