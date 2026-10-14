
package com.fittrack_app.fittrack.modules.producte.controller;

import com.fittrack_app.fittrack.modules.producte.dto.ProductCreateDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProductResponseDto response =
                productService.createProduct(
                        dto,
                        getEmail(userDetails)
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.getAllProducts(
                        getEmail(userDetails),
                        pageable
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.searchProduct(
                        query,
                        getEmail(userDetails),
                        pageable
                )
        );
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDto> getByBarcode(
            @PathVariable String barcode,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                productService.getProductByBarcode(
                        barcode,
                        getEmail(userDetails)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                productService.getProductById(
                        id,
                        getEmail(userDetails)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        productService.deleteProduct(
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
