package com.example.forum.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Фильтр аутентификации, использующий JWT для проверки запросов. Этот фильтр проверяет наличие
 * токена JWT в заголовках авторизации входящих запросов и аутентифицирует пользователя, если токен
 * валиден.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  /**
   * Конструктор для создания экземпляра фильтра аутентификации JWT.
   *
   * @param jwtTokenProvider Провайдер токенов JWT, используемый для валидации и извлечения данных
   *     из токена.
   * @param userDetailsService Сервис для загрузки данных пользователя по имени пользователя из
   *     токена.
   */
  public JwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Метод фильтрации для внутренней обработки HTTP запросов. Извлекает токен из запроса, валидирует
   * его и аутентифицирует пользователя в контексте безопасности Spring.
   *
   * @param request HTTP запрос.
   * @param response HTTP ответ.
   * @param filterChain Цепочка фильтрации для передачи управления следующему фильтру.
   * @throws ServletException В случае ошибок фильтрации сервлета.
   * @throws IOException В случае ошибок ввода/вывода.
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = getTokenFromRequest(request);
    if (token != null && jwtTokenProvider.validateToken(token)) {
      String username = jwtTokenProvider.getUsernameFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  /**
   * Вспомогательный метод для извлечения токена JWT из заголовка авторизации запроса.
   *
   * @param request HTTP запрос.
   * @return Строка токена или {@code null}, если токен отсутствует или не начинается с 'Bearer '.
   */
  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
