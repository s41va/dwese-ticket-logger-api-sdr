package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.utils;

import io.jsonwebtoken.Claims;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private KeyPair jwtKeyPair;

    private static final long JWT_EXPIRATION = 3_600_000L;

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
                .verifyWith(jwtKeyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String username, List<String> roles){
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(jwtKeyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

    }

    /**
     * Valida un token JWT verificando:
     * 1) La firma con la clave pública del certificado (RSA).
     * 2) Que el subject (username) coincide con el esperado.
     * 3) Que el token no ha expirado.
     * * Si el token está mal formado, expirado, o la firma no es válida -> devuelve false.
     */
    public boolean validateToken(String token, String username) {
        try {
            // 1) Parseo del token + verificación de firma con la PUBLIC KEY del KeyPair
            Claims claims = Jwts.parser()
                    .verifyWith(jwtKeyPair.getPublic())  // valida firma RSA
                    .build()
                    .parseSignedClaims(token)           // token firmado (JWS)
                    .getPayload();                      // claims ya verificados

            // 2) Comprobación del subject (sub)
            String tokenUsername = claims.getSubject();
            if (tokenUsername == null || !tokenUsername.equals(username)) {
                return false;
            }

            // 3) Comprobación de expiración (exp)
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());

        } catch (Exception e) {
            // Firma inválida, token expirado, token manipulado, etc.
            return false;
        }
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
