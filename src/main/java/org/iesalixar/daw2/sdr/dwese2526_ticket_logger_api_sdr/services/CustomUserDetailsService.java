package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);


    @Autowired
    private UsersRepository userRepository;


    /**
     * Carga los detalles de autenticación/autorización del usuario a partir del "username".
     *
     * <p>En este proyecto {@code username == email}, por lo que se consulta
     * {@link UsersRepository#findByEmail(String)}.</p>
     *
     * <p>Devuelve un {@link UserDetails} con:
     * <ul>
     *   <li>{@code username}: email</li>
     *   <li>{@code password}: hash BCrypt almacenado en {@code password_hash}</li>
     *   <li>{@code authorities}: roles tipo {@code ROLE_USER}, {@code ROLE_ADMIN}, etc.</li>
     *   <li>{@code disabled}: basado en {@code active}</li>
     * </ul>
     * </p>
     *
     * @param username email introducido en el formulario de login.
     * @return {@link UserDetails} que Spring Security usará para autenticar y autorizar.
     * @throws UsernameNotFoundException si no existe un usuario con ese email.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log de entrada (sin datos sensibles; el email es aceptable en muchos entornos,
        // aunque en producción a veces se enmascara).
        log.debug("Entrando en loadUserByUsername(username={})", username);


        final String email = username;


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No se encontró usuario con email={}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });


        // Construimos el UserDetails de Spring Security.
        // OJO: nunca loguear contraseñas ni hashes.
        // Se necesita el org.springframework.security.core.userdetails para poder diferenciarlo de nuestro User
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(
                        user.getRoles().stream()
                                .map(role -> role.getName()) // ROLE_ADMIN, ROLE_USER...
                                .collect(Collectors.toList())
                                .toArray(new String[0])
                )
                // Versión mínima: sin reglas extra aún.
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();


        // Log de salida
        log.debug("Saliendo de loadUserByUsername(email={}) -> authorities={}",
                user.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList())
        );


        return userDetails;
    }
}