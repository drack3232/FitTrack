package com.fittrack_app.fittrack.modules.nutrition.controller;
import com.fittrack_app.fittrack.modules.nutrition.dto.DailyNutritionSummaryDto;
import com.fittrack_app.fittrack.modules.nutrition.service.DailyNutritionService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/nutrition")
public class DailyNutritionController {

    private final DailyNutritionService dailyNutritionService;

    @GetMapping("/summary")
    public ResponseEntity<DailyNutritionSummaryDto> getDailySummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        DailyNutritionSummaryDto response =
                dailyNutritionService.getDailySummary(
                        date,
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
