package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.config;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura el filtro de seguridad para las solicitudes HTTP, especificando las
     * rutas permitidas y los roles necesarios para acceder a diferentes endpoints.
     *
     * @param http instancia de {@link HttpSecurity} para configurar la seguridad.
     * @return una instancia de {@link SecurityFilterChain} que contiene la configuración de seguridad.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1) API REST: normalmente desactivas CSRF (no hay sesión/cookies)
                .csrf(csrf -> csrf.disable())

                // 2) Sin sesión (stateless) porque autenticamos por token en cada request
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3) No queremos formLogin ni redirecciones
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 4) Manejo de errores típico API: 401 / 403
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )

                // 5) Autorización por rutas (ajusta a /api/**)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()

                        // Ejemplos por roles (tu lógica, adaptada a /api)
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/regions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/provinces/**").hasRole("MANAGER")
                        .requestMatchers("/api/profile/**").hasRole("USER")

                        // Todo lo demás requiere token válido
                        .anyRequest().authenticated()
                )

                // 6) Añadir tu filtro JWT antes del filtro de auth de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }





    /**
     * Configura el codificador de contraseñas para cifrar las contraseñas de los usuarios
     * utilizando BCrypt.
     *
     * @return una instancia de {@link PasswordEncoder} que utiliza BCrypt para cifrar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}