package com.fittrack_app.fittrack.modules.nutrition.controller;

import com.fittrack_app.fittrack.modules.nutrition.dto.FoodEntryRequestDto;
import com.fittrack_app.fittrack.modules.nutrition.dto.FoodEntryResponseDto;
import com.fittrack_app.fittrack.modules.nutrition.service.FoodEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nutrition/foods")
@RequiredArgsConstructor
public class FoodEntryController {

    private final FoodEntryService foodEntryService;

    @PostMapping
    public ResponseEntity<FoodEntryResponseDto> addFood(
            @Valid @RequestBody FoodEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodEntryResponseDto response = foodEntryService.addFood(
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<FoodEntryResponseDto>> getFoodsByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<FoodEntryResponseDto> response =
                foodEntryService.getFoodsByDate(
                        date,
                        getEmail(userDetails)
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodEntryResponseDto> getFood(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodEntryResponseDto response = foodEntryService.getFood(
                id,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodEntryResponseDto> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodEntryResponseDto response = foodEntryService.updateFood(
                id,
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        foodEntryService.deleteFood(
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
