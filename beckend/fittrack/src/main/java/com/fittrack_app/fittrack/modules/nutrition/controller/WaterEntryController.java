package com.fittrack_app.fittrack.modules.nutrition.controller;


import com.fittrack_app.fittrack.modules.nutrition.dto.WaterEntryRequestDto;
import com.fittrack_app.fittrack.modules.nutrition.dto.WaterEntryResponseDto;
import com.fittrack_app.fittrack.modules.nutrition.service.WaterEntryService;
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
@RequestMapping("/api/v1/nutrition/water")
@RequiredArgsConstructor
public class WaterEntryController {
    private final WaterEntryService waterEntryService;

    @PostMapping
    public ResponseEntity<WaterEntryResponseDto> addWater(
            @Valid @RequestBody WaterEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        WaterEntryResponseDto response = waterEntryService.addWater(
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WaterEntryResponseDto>> getWaterByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<WaterEntryResponseDto> response =
                waterEntryService.getWaterByDate(
                        date,
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalWater(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long total = waterEntryService.getTotalWater(
                date,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(total);
    }
    @PutMapping("/{id}")
    public ResponseEntity<WaterEntryResponseDto> updateWater(
            @PathVariable Long id,
            @Valid @RequestBody WaterEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        WaterEntryResponseDto response = waterEntryService.updateWater(
                id,
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWater(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        waterEntryService.deleteWater(
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
