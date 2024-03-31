package com.example.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурация безопасности веб-приложения на основе Spring Security. Определяет глобальные
 * настройки безопасности, такие как стратегии аутентификации, шифрование паролей и управление
 * сессиями.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  /**
   * Конструктор для SecurityConfig, инициализирующий провайдер токенов JWT и сервис деталей
   * пользователей.
   *
   * @param jwtTokenProvider Провайдер токенов JWT, используемый для аутентификации запросов.
   * @param userDetailsService Сервис для загрузки информации о пользователе по его имени.
   */
  public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Bean для кодирования паролей с использованием BCrypt.
   *
   * @return Экземпляр PasswordEncoder для шифрования паролей.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Bean для AuthenticationManager, используемый Spring Security для аутентификации пользователей.
   *
   * @param authenticationConfiguration Конфигурация аутентификации, предоставляемая Spring
   *     Security.
   * @return Экземпляр AuthenticationManager.
   * @throws Exception в случае ошибок конфигурации.
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * Конфигурирует цепочку фильтров безопасности для определения правил доступа к ресурсам,
   * управления сессиями и добавления фильтров аутентификации JWT.
   *
   * @param http HttpSecurity для настройки защиты веб-запросов.
   * @return Сконфигурированный экземпляр SecurityFilterChain.
   * @throws Exception в случае ошибок конфигурации безопасности.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("api/v1/sign-in/**", "/h2-console/**")
                    .permitAll()
                    .requestMatchers("api/v1/sign-up/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
            UsernamePasswordAuthenticationFilter.class)
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
    return http.build();
  }
}
