package com.example.forum.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.forum.config.JwtTokenProvider;
import com.example.forum.dto.UserDTO;
import com.example.forum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthControllerTest {

  private UserService userService;
  private JwtTokenProvider jwtTokenProvider;
  private AuthenticationManager authenticationManager;
  private PasswordEncoder passwordEncoder;
  private AuthController authController;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    jwtTokenProvider = mock(JwtTokenProvider.class);
    authenticationManager = mock(AuthenticationManager.class);
    passwordEncoder = mock(PasswordEncoder.class);
    authController =
        new AuthController(userService, jwtTokenProvider, authenticationManager, passwordEncoder);
  }

  @Test
  void register_shouldReturnToken() {
    UserDTO userDto = new UserDTO();
    userDto.setUsername("testUser");
    userDto.setPassword("password");

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

    ResponseEntity<String> response = authController.register(userDto);

    assertEquals(ResponseEntity.ok("token"), response);
    verify(userService).registerUser(eq("testUser"), eq("encodedPassword"));
  }

  @Test
  void login_shouldReturnToken_onSuccessfulAuthentication() {
    UserDTO userDto = new UserDTO();
    userDto.setUsername("testUser");
    userDto.setPassword("password");

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

    ResponseEntity<?> response = authController.login(userDto);

    assertEquals(ResponseEntity.ok("token"), response);
  }

  @Test
  void login_shouldReturnUnauthorized_onBadCredentials() {
    UserDTO userDto = new UserDTO();
    userDto.setUsername("testUser");
    userDto.setPassword("wrongPassword");

    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

    ResponseEntity<?> response = authController.login(userDto);

    assertEquals(
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"),
        response);
  }
}
