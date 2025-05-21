package monostore.backend.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

  private Key key;

  @PostConstruct
  public void init() {
    String secret = "0a732767d5ee374d4a3478077f84a009863";
    key = Keys.hmacShaKeyFor(secret.getBytes());
  }

    public String generateToken(String email, String role) {
        // Set expiration time to 1 day
        long oneDayMs = 24 * 60 * 60 * 1000L;
        return Jwts.builder()
            .setSubject(email)
            .claim("email", email)
            .claim("role", role)

            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + oneDayMs))
            .signWith(key)
            .compact();
    }

    public Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }
}
