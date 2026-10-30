
package com.fittrack_app.fittrack.modules.activity.controller;

import com.fittrack_app.fittrack.modules.activity.dto.ActivityRequestDto;
import com.fittrack_app.fittrack.modules.activity.dto.ActivityResponseDto;
import com.fittrack_app.fittrack.modules.activity.dto.DailyActivitySummaryDto;
import com.fittrack_app.fittrack.modules.activity.service.ActivityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;


    @PostMapping
    public ResponseEntity<ActivityResponseDto> addActivity(
            @Valid @RequestBody ActivityRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        ActivityResponseDto response = activityService.addActivity(
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDto>> getActivitiesByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ActivityResponseDto> response =
                activityService.getActivitiesByDate(
                        date,
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<DailyActivitySummaryDto> getDailySummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        DailyActivitySummaryDto response =
                activityService.getDailySummary(
                        date,
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        ActivityResponseDto response = activityService.getActivity(
                id,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        ActivityResponseDto response = activityService.updateActivity(
                id,
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        activityService.deleteActivity(
                id,
                getEmail(userDetails)
        );

        return ResponseEntity.noContent().build();
    }

    private String getEmail(UserDetails userDetails) {

        return userDetails == null
                ? null
                : userDetails.getUsername();
    }
}
