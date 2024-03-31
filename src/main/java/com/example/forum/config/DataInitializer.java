package com.example.forum.config;

import com.example.forum.entity.User;
import com.example.forum.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Класс для инициализации начальных данных в базе данных. Этот компонент активируется только в
 * профилях 'dev' и 'init', что позволяет выполнить предварительную настройку данных для разработки
 * и инициализации. В частности, создает пользователя-администратора, если он еще не существует.
 */
@Component
@Profile({"dev", "init"})
public class DataInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Конструктор для DataInitializer.
   *
   * @param userRepository Репозиторий для взаимодействия с пользователями в базе данных.
   * @param passwordEncoder Кодировщик паролей, используемый для шифрования паролей пользователей.
   */
  @Autowired
  public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Инициализирует пользователя-администратора при запуске приложения. Если пользователь с именем
   * 'admin' не найден, создает нового пользователя с этим именем и паролем 'admin'.
   */
  @PostConstruct
  public void initAdminUser() {
    if (userRepository.findByUsername("admin").isEmpty()) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword(passwordEncoder.encode("admin"));
      userRepository.save(admin);
    }
  }
}
