package com.fittrack_app.fittrack.modules.user.controller;

import com.fittrack_app.fittrack.modules.user.dto.UserRegistrationDto;
import com.fittrack_app.fittrack.modules.user.dto.UserResponseDto;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto){
        UserResponseDto responseDto = userService.registerUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
