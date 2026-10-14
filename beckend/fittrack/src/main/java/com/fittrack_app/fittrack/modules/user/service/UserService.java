
package com.fittrack_app.fittrack.modules.user.service;

import com.fittrack_app.fittrack.modules.user.dto.*;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        User user = User.builder()
                .age(dto.getAge())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .passwordHash(
                        passwordEncoder.encode(dto.getPassword())
                )
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

    @Transactional(readOnly = true)
    public AuthenticationResponseDto loginUser(UserLoginDto dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthenticationResponseDto(jwtToken);
    }
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUserProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .gender(user.getGender())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .createdAt(user.getCreatedAt())
                .build();
    }

//for user  can update his statistic
    @Transactional
    public UserResponseDto updateProfile(
            String email,
            UserUpdateDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }

        if (dto.getHeight() != null) {
            user.setHeight(dto.getHeight());
        }

        if (dto.getWeight() != null) {
            user.setWeight(dto.getWeight());
        }

        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .gender(savedUser.getGender())
                .age(savedUser.getAge())
                .height(savedUser.getHeight())
                .weight(savedUser.getWeight())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}
