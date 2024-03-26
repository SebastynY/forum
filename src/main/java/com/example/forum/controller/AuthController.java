package com.example.forum.controller;

import com.example.forum.config.JwtTokenProvider;
import com.example.forum.dto.UserDTO;
import com.example.forum.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class AuthController {
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  public AuthController(
      UserService userService,
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/sign-up")
  public ResponseEntity<String> register(@RequestBody UserDTO userDto) {
    userService.registerUser(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()));
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
    String token = jwtTokenProvider.generateToken(authentication);

    return ResponseEntity.ok(token);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<?> login(@RequestBody UserDTO userDto) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  userDto.getUsername(), userDto.getPassword()));
      String token = jwtTokenProvider.generateToken(authentication);
      return ResponseEntity.ok(token);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
  }
}
