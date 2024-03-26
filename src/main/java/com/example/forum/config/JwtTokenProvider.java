package com.example.forum.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Компонент для обработки JWT токенов, включая их создание, валидацию и извлечение информации.
 * Использует библиотеку JJWT для работы с JWT.
 */
@Component
public class JwtTokenProvider {
  private final Key jwtSecret;
  private final long jwtExpirationMs = 86400000;

  /**
   * Конструктор по умолчанию для {@code JwtTokenProvider}. Инициализирует ключ для подписи токенов
   * и устанавливает время истечения токена.
   */
  public JwtTokenProvider() {
    this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  }

  /**
   * Генерирует токен JWT для аутентифицированного пользователя.
   *
   * @param authentication Объект аутентификации, содержащий основные данные пользователя.
   * @return Строка, представляющая сгенерированный JWT токен.
   */
  public String generateToken(Authentication authentication) {
    UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(jwtSecret)
        .compact();
  }

  /**
   * Проверяет валидность переданного JWT токена.
   *
   * @param token Строка JWT токена для валидации.
   * @return {@code true}, если токен валиден, иначе {@code false}.
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Извлекает имя пользователя из JWT токена.
   *
   * @param token Строка JWT токена.
   * @return Имя пользователя, закодированное в токене.
   */
  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    return claims.getSubject();
  }
}
