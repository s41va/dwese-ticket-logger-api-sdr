package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO reutilizable para actualizar usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersUpdateDTO {


    @NotNull(message = "{msg.user.id.notnull}")
    private Long id;


    @Email(message = "{msg.user.email.invalid}")
    @NotBlank(message = "{msg.user.username.notblank}")
    @Size(min = 4, max = 100, message = "{msg.user.username.size}")
    private String email;


    /*@NotBlank(message = "{msg.user.passwordHash.notblank}")
    @Size(min = 8, max = 500, message = "{msg.user.passwordHash.size}")
    private String passwordHash;*/

    @NotNull(message = "{msg.user.active.notnull}")
    private boolean active;


    @NotNull(message = "{msg.user.accountNonLocked.notnull}")
    private boolean accountNonLocked;


    @PastOrPresent(message = "{msg.user.lastPasswordChange.pastorpresent}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime lastPasswordChange;


    @FutureOrPresent(message = "{msg.user.passwordExpiresAt.futureorpresent}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime passwordExpiresAt;


    @Min(value = 0, message = "{msg.user.failedLoginAttempts.min}")
    private Integer failedLoginAttempts;


    @NotNull(message = "{msg.user.emailVerified.notnull}")
    private boolean emailVerified;


    @NotNull(message = "{msg.user.mustChangePassword.notnull}")
    private boolean mustChangePassword;


    // ─────────────────────────────────────
    // Roles seleccionados (ids de Role) - OBLIGATORIOS
    // ─────────────────────────────────────
    @NotEmpty(message = "{msg.user.roles.notempty}")
    private Set<Long> roleIds = new HashSet<>();
}
