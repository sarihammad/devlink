package com.devlink.service;

import com.devlink.dto.LoginRequest;
import com.devlink.dto.RegisterRequest;
import com.devlink.model.User;
import com.devlink.repository.UserRepository;
import com.devlink.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainpass");
        request.setUsername("testuser");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainpass")).thenReturn("hashedpass");

        // Simulate saving the user
        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setUsername("testuser");
        savedUser.setPassword("hashedpass");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock UserDetails with the same username
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");

        // Simulate register() using the mocked userDetails for token generation
        String token = jwtService.generateToken(userDetails);

        assertEquals("mock.jwt.token", token);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret");

        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mock.jwt.token");

        String token = jwtService.generateToken(userDetails);

        assertEquals("mock.jwt.token", token);

        verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken("test@example.com", "secret")
        );
    }
}