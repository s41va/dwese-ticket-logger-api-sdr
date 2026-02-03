package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities;

import jakarta.persistence.*;
import lombok.*;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad JPA para la tabla 'users'.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {


    /** BIGINT AUTO_INCREMENT PRIMARY KEY */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /** VARCHAR(100) NOT NULL UNIQUE */
    @Column(name = "email", nullable = false, unique = true, length = 40)
    private String email;


    /** VARCHAR(500) NOT NULL */
    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;


    /** BOOLEAN NOT NULL DEFAULT TRUE */
    @Column(name = "active", nullable = false)
    private boolean active;


    /** BOOLEAN NOT NULL DEFAULT TRUE */
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;


    /** DATETIME NULL */
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;


    /** DATETIME NULL */
    @Column(name = "password_expires_at")
    private LocalDateTime passwordExpiresAt;


    /** INT DEFAULT 0 */
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;


    /** BOOLEAN NOT NULL DEFAULT FALSE */
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;


    /** BOOLEAN NOT NULL DEFAULT FALSE */
    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;


    /** Relación 1:1 con la entidad UserProfile */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserProfile profile;


    /** Relación N:M con Role a través de la tabla intermedia 'user_roles'. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();




    /** Constructor completo (sin id autogenerado). */
    public User(String username,
                String passwordHash,
                Boolean active,
                Boolean accountNonLocked,
                LocalDateTime lastPasswordChange,
                LocalDateTime passwordExpiresAt,
                Integer failedLoginAttempts,
                Boolean emailVerified,
                Boolean mustChangePassword) {
        this.email = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.accountNonLocked = accountNonLocked;
        this.lastPasswordChange = lastPasswordChange;
        this.passwordExpiresAt = passwordExpiresAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.emailVerified = emailVerified;
        this.mustChangePassword = mustChangePassword;
    }
}
