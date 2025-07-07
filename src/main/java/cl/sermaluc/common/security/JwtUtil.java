package cl.sermaluc.common.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

	private Key key;
	
	@PostConstruct
	public void init() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		
		if (keyBytes.length < 32) {
			throw new IllegalArgumentException("La clave secreta debe tener minimo 32 caracteres para el algoritmo HS256.");
		}
		
		this.key = Keys.hmacShaKeyFor(keyBytes);
    }
	
	public String generateToken(UUID userId) {
		return Jwts.builder()
				.setSubject(userId.toString())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 300000))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(this.key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
