package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {


    private Long id;


    private String email;


    // En este CRUD inicial tratamos la contraseña como texto plano,
    // mapeando directamente el campo passwordHash de la entidad.
    /*private String passwordHash;*/


    private boolean active;


    private boolean accountNonLocked;


    private LocalDateTime lastPasswordChange;


    private LocalDateTime passwordExpiresAt;


    private Integer failedLoginAttempts;


    private boolean emailVerified;


    private boolean mustChangePassword;


    // Roles asociados al usuario (nombres técnicos: ROLE_ADMIN, ROLE_USER, etc.)
    private Set<String> roles;
}
