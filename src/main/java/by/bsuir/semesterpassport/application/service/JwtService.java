package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Используем одну переменную для секрета из application.properties
    @Value("${application.security.jwt.secret-key:default_secret_key_32_characters_long_min}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}") // по умолчанию 24 часа
    private long jwtExpiration;

    // --- ИЗВЛЕЧЕНИЕ ДАННЫХ ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // --- ГЕНЕРАЦИЯ ТОКЕНА ---

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        // Зашиваем все нужные фронтенду данные прямо в "паспорт" (токен)
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("groupNumber", user.getGroupNumber());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // --- ВАЛИДАЦИЯ ---

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- СЛУЖЕБНЫЕ МЕТОДЫ ---

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        // Кодируем строку секрета в байты для алгоритма HMAC
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}