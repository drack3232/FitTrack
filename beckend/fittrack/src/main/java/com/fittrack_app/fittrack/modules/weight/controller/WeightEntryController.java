
package com.fittrack_app.fittrack.modules.weight.controller;

import com.fittrack_app.fittrack.modules.weight.dto.WeightEntryRequestDto;
import com.fittrack_app.fittrack.modules.weight.dto.WeightEntryResponseDto;
import com.fittrack_app.fittrack.modules.weight.dto.WeightProgressDto;
import com.fittrack_app.fittrack.modules.weight.service.WeightEntryService;

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
@RequestMapping("/api/v1/weights")
@RequiredArgsConstructor
public class WeightEntryController {

    private final WeightEntryService weightEntryService;

    @PostMapping
    public ResponseEntity<WeightEntryResponseDto> addWeight(
            @Valid @RequestBody WeightEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        WeightEntryResponseDto response = weightEntryService.addWeight(
                dto,
                getEmail(userDetails)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WeightEntryResponseDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                weightEntryService.getWeightHistory(
                        getEmail(userDetails)
                )
        );
    }

    @GetMapping("/period")
    public ResponseEntity<List<WeightEntryResponseDto>> getHistoryByPeriod(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                weightEntryService.getWeightHistoryByPeriod(
                        startDate,
                        endDate,
                        getEmail(userDetails)
                )
        );
    }

    @GetMapping("/progress")
    public ResponseEntity<WeightProgressDto> getProgress(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                weightEntryService.getProgress(
                        getEmail(userDetails)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeightEntryResponseDto> getWeightEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                weightEntryService.getWeightEntry(
                        id,
                        getEmail(userDetails)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeightEntryResponseDto> updateWeight(
            @PathVariable Long id,
            @Valid @RequestBody WeightEntryRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                weightEntryService.updateWeight(
                        id,
                        dto,
                        getEmail(userDetails)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeight(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        weightEntryService.deleteWeight(
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
