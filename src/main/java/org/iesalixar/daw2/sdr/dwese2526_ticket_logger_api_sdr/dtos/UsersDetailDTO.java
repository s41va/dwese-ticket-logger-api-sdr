package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de detalle de User.
 * Pensado para vistas de detalle donde, en un futuro,
 * se puedan incluir colecciones relacionadas (roles, tickets, etc.).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDetailDTO {


    private Long id;


    private String email;


    private boolean active;


    private boolean accountNonLocked;


    private LocalDateTime lastPasswordChange;


    private LocalDateTime passwordExpiresAt;


    private Integer failedLoginAttempts;


    private boolean emailVerified;


    private boolean mustChangePassword;


    // ────────────────────────────────────────────
    // Campos del perfil del usuario (UserProfile)
    // ────────────────────────────────────────────


    private String firstName;


    private String lastName;


    private String phoneNumber;


    private String profileImage;


    private String bio;


    private String locale;


    // ────────────────────────────────────────────
    // Roles del usuario (nombres de rol)
    // ────────────────────────────────────────────
    private Set<String> roles;
}
