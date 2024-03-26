package com.example.forum.service;

import com.example.forum.entity.User;
import com.example.forum.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями форума. Реализует интерфейс UserDetailsService, необходимый
 * для интеграции с механизмом аутентификации Spring Security.
 */
@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;

  /**
   * Конструктор для создания экземпляра сервиса работы с пользователями.
   *
   * @param userRepository Репозиторий для доступа к данным пользователей в базе данных.
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Регистрирует нового пользователя в системе.
   *
   * @param username Имя пользователя.
   * @param password Пароль пользователя.
   * @return Объект User, представляющий зарегистрированного пользователя.
   */
  public User registerUser(String username, String password) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    return userRepository.save(user);
  }

  /**
   * Загружает данные пользователя по его имени пользователя для аутентификации.
   *
   * @param username Имя пользователя.
   * @return Объект UserDetails, содержащий информацию о пользователе.
   * @throws UsernameNotFoundException если пользователь с таким именем пользователя не найден.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities("USER")
        .build();
  }
}
