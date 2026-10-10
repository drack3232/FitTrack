package com.fittrack_app.fittrack.modules.user.service;

import com.fittrack_app.fittrack.modules.user.dto.AuthenticationResponseDto;
import com.fittrack_app.fittrack.modules.user.dto.UserLoginDto;
import com.fittrack_app.fittrack.modules.user.dto.UserRegistrationDto;
import com.fittrack_app.fittrack.modules.user.dto.UserResponseDto;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("User with email address like this doesnt exist");
        }
        User user = User.builder()
                .age(dto.getAge())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .age(savedUser.getAge())
                .email(savedUser.getEmail())
                .gender(savedUser.getGender())
                .createdAt(savedUser.getCreatedAt())
                .height(savedUser.getHeight())
                .weight(savedUser.getWeight())
                .build();
    }

    @Transactional
    public AuthenticationResponseDto loginUser(UserLoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );


        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Incorrect email or password"));

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponseDto(jwtToken);

    }
}
