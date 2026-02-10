package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {


    /**
     * Clave secreta para firmar y verificar el token JWT.
     * La clave se inyecta desde el archivo application.properties para mantener
     * la seguridad y permitir flexibilidad en entornos diferentes.
     * * @Value obtiene el valor configurado en application.properties, por ejemplo:
     * jwt.secret=tu-clave-super-segura-de-al-menos-32-caracteres
     */
    @Value("${jwt.secret}")
    private String secretKeyFromProperties;

    /**
     * Obtiene la clave de firma (SecretKey) usando la clave secreta inyectada.
     * Keys.hmacShaKeyFor convierte la clave en un objeto SecretKey.
     * * Es importante que la clave tenga al menos 256 bits (32 caracteres) para HS256.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyFromProperties.getBytes());
    }

    /**
     * Extrae el nombre de usuario (claim "sub") del token.
     * El nombre de usuario suele ser el identificador del usuario que está autenticado.
     * * @param token el token JWT del cual se extraerá el claim.
     * @return el nombre de usuario contenido en el token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String username, List<String> roles){
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();

    }
    public boolean validateToken(String token, String username){
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
