
package com.fittrack_app.fittrack.modules.analytics.controller;

import com.fittrack_app.fittrack.modules.analytics.dto.AnalyticsResponseDto;
import com.fittrack_app.fittrack.modules.analytics.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponseDto> getAnalytics(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @AuthenticationPrincipal UserDetails userDetails) {

        AnalyticsResponseDto response =
                analyticsService.getAnalytics(
                        startDate,
                        endDate,
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
