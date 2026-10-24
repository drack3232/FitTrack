package com.fittrack_app.fittrack.modules.goals.controller;

import com.fittrack_app.fittrack.modules.goals.dto.CalorieCalculationResponseDto;
import com.fittrack_app.fittrack.modules.goals.dto.UserGoalRequestDto;
import com.fittrack_app.fittrack.modules.goals.dto.UserGoalResponseDto;
import com.fittrack_app.fittrack.modules.goals.service.UserGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class UserGoalController {
    private final UserGoalService userGoalService;

    @PutMapping("/me")
    public ResponseEntity<UserGoalResponseDto> saveGoal(
            @Valid @RequestBody UserGoalRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserGoalResponseDto response =
                userGoalService.saveGoal(
                        dto,
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/me")
    public ResponseEntity<UserGoalResponseDto> getGoal(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserGoalResponseDto response =
                userGoalService.getGoal(
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/me/calculations")
    public ResponseEntity<CalorieCalculationResponseDto> getCalculations(
            @AuthenticationPrincipal UserDetails userDetails) {

        CalorieCalculationResponseDto response =
                userGoalService.getCalculations(
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }

    private String getEmail(UserDetails userDetails) {

        return userDetails == null
                ? null
                : userDetails.getUsername();
    }
}
