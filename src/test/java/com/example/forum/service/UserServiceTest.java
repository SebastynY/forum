package com.example.forum.service;


import com.example.forum.entity.User;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void registerUser_ValidInput_ReturnsUser() {
    User user = new User();
    user.setUsername("TestUser");
    user.setPassword("password");

    when(userRepository.save(any(User.class))).thenReturn(user);

    User registeredUser = userService.registerUser("TestUser", "password");

    assertThat(registeredUser.getUsername()).isEqualTo("TestUser");
    assertThat(registeredUser.getPassword()).isEqualTo("password");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  public void loadUserByUsername_ExistingUsername_ReturnsUserDetails() {
    User user = new User();
    user.setUsername("ExistingUser");
    user.setPassword("password");

    when(userRepository.findByUsername("ExistingUser")).thenReturn(Optional.of(user));

    UserDetails userDetails = userService.loadUserByUsername("ExistingUser");

    assertThat(userDetails.getUsername()).isEqualTo("ExistingUser");
    assertThat(userDetails.getPassword()).isEqualTo("password");
    verify(userRepository, times(1)).findByUsername("ExistingUser");
  }

  @Test
  public void loadUserByUsername_NonExistingUsername_ThrowsUsernameNotFoundException() {
    when(userRepository.findByUsername("NonExistingUser")).thenReturn(Optional.empty());

    assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy(() -> userService.loadUserByUsername("NonExistingUser"))
        .withMessage("User not found with username: NonExistingUser");

    verify(userRepository, times(1)).findByUsername("NonExistingUser");
  }
}
