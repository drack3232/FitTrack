package com.fittrack_app.fittrack.modules.user.service;

import com.fittrack_app.fittrack.modules.user.dto.UserRegistrationDto;
import com.fittrack_app.fittrack.modules.user.dto.UserResponseDto;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto){
        if (userRepository.existsByEmail(dto.getEmail())){
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
    public double calculaterBMR(User user){
        double bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge());
        if ("MALE".equalsIgnoreCase(user.getGender())) {
            return bmr + 5;
        } else {
            return bmr - 161;
        }
    }
}
