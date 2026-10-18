
package com.fittrack_app.fittrack.modules.producte.controller;

import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.service.FavoriteProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class FavoriteProductController {

    private final FavoriteProductService favoriteProductService;


    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        favoriteProductService.addFavorite(
                id,
                userDetails.getUsername()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/favorites")
    public ResponseEntity<Page<ProductResponseDto>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<ProductResponseDto> favorites =
                favoriteProductService.getFavorites(
                        userDetails.getUsername(),
                        pageable
                );

        return ResponseEntity.ok(favorites);
    }
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        favoriteProductService.removeFavorite(
                id,
                userDetails.getUsername()
        );

        return ResponseEntity.noContent().build();
    }
}
