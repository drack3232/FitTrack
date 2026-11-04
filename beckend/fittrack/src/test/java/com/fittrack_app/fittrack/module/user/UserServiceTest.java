package com.fittrack_app.fittrack.module.user;
import com.fittrack_app.fittrack.modules.user.dto.UserLoginDto;
import com.fittrack_app.fittrack.modules.user.dto.UserRegistrationDto;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.modules.user.service.UserService;
import com.fittrack_app.fittrack.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail()).thenReturn("test@gmail.com");
        when(dto.getPassword()).thenReturn("password123");

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        assertDoesNotThrow(() -> userService.registerUser(dto));

        verify(userRepository).save(argThat(user ->
                "test@gmail.com".equals(user.getEmail())
                        && "encodedPassword".equals(user.getPasswordHash())
        ));
    }

    @Test
    void shouldRejectDuplicateEmail() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail()).thenReturn("test@gmail.com");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(dto)
        );

        verify(userRepository).existsByEmail("test@gmail.com");

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldRejectInvalidLogin() {

        UserLoginDto dto = mock(UserLoginDto.class);

        when(dto.getEmail()).thenReturn("test@gmail.com");
        when(dto.getPassword()).thenReturn("wrongPassword");

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenThrow(
                new BadCredentialsException("Invalid credentials")
        );

        assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(dto)
        );

        verify(authenticationManager)
                .authenticate(any(Authentication.class));
    }
}
