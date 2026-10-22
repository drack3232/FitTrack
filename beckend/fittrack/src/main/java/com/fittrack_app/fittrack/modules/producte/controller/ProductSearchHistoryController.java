package com.fittrack_app.fittrack.modules.producte.controller;


import com.fittrack_app.fittrack.modules.producte.dto.ProductSearchHistoryResponseDto;
import com.fittrack_app.fittrack.modules.producte.service.ProductSearchHistoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/search-history")
@RequiredArgsConstructor
public class ProductSearchHistoryController {

    private final ProductSearchHistoryService historyService;
    @PostMapping
    public ResponseEntity<Void> recordSearch(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        historyService.recordSearch(
                query,
                getEmail(userDetails)
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductSearchHistoryResponseDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProductSearchHistoryResponseDto> history =
                historyService.getHistory(
                        getEmail(userDetails),
                        pageable
                );

        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoryEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        historyService.deleteHistoryEntry(
                id,
                getEmail(userDetails)
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        historyService.clearHistory(
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
