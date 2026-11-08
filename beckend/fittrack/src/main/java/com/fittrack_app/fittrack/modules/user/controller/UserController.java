package com.fittrack_app.fittrack.modules.user.controller;
import com.fittrack_app.fittrack.modules.user.dto.*;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.modules.user.service.UserService;
import com.fittrack_app.fittrack.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto dto){
        UserResponseDto responseDto = userService.registerUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @Valid
            @RequestBody UserLoginDto loginDto){

        AuthenticationResponseDto response =
                userService.loginUser(loginDto);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/me(test)")
//    public ResponseEntity<UserResponseDto> me (
//            @AuthenticationPrincipal UserDetails userDetails
//
//            ){
//        UserResponseDto response =
//                userService.getCurrentUserProfile(
//                        userDetails.getUsername()
//                        );
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/me")
public ResponseEntity<UserProfileDto> getMyProfile(
        @AuthenticationPrincipal UserDetails principal) {

    if (principal == null) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authentication required"
        );
    }

    User user = userRepository
            .findByEmail(principal.getUsername())
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "User not found"
                    )
            );

    UserProfileDto response = new UserProfileDto(
            user.getId(),
            user.getEmail(),
            user.getGender(),
            user.getAge(),
            user.getHeight(),
            user.getWeight()
    );

    return ResponseEntity.ok(response);
}
}
