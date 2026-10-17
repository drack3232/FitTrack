
package com.fittrack_app.fittrack.modules.producte.service;

import com.fittrack_app.fittrack.modules.producte.dto.ProductCreateDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductUpdateDto;
import com.fittrack_app.fittrack.modules.producte.entity.Product;
import com.fittrack_app.fittrack.modules.producte.repository.ProductRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponseDto createProduct(
            ProductCreateDto dto,
            String email) {

        requireAuthentication(email);

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );

        double totalMacros =
                dto.getProtein() +
                        dto.getFat() +
                        dto.getCarbs();

        if (totalMacros > 100.000001) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Total macros cannot exceed 100g"
            );
        }

        String barcode = dto.getBarcode();

        if (barcode != null &&
                productRepository.existsByBarcode(barcode)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product with this barcode already exists"
            );
        }

        Product product = Product.builder()
                .name(dto.getName().trim())
                .brand(dto.getBrand())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .carbs(dto.getCarbs())
                .barcode(barcode)
                .owner(owner)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(
            String email,
            Pageable pageable) {

        validatePageable(pageable);

        return productRepository
                .findVisibleProducts(email, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProduct(
            String query,
            String email,
            Pageable pageable) {

        validatePageable(pageable);

        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search query cannot be empty"
            );
        }

        return productRepository
                .searchVisibleProducts(
                        email,
                        query.trim(),
                        pageable
                )
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(
            Long id,
            String email) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        checkVisibility(product, email);

        return mapToDto(product);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByBarcode(
            String barcode,
            String email) {

        Product product = productRepository
                .findByBarcode(barcode)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        checkVisibility(product, email);

        return mapToDto(product);
    }

    @Transactional
    public void deleteProduct(Long id, String email) {

        requireAuthentication(email);

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        checkVisibility(product, email);

        if (product.getOwner() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Cannot delete a public product"
            );
        }

        productRepository.delete(product);
    }

    private void checkVisibility(Product product, String email) {

        User owner = product.getOwner();

        if (owner != null &&
                !owner.getEmail().equals(email)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }
    }

    private void requireAuthentication(String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }
    }

    private void validatePageable(Pageable pageable) {

        if (pageable.isUnpaged() ||
                pageable.getPageSize() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }

    public ProductResponseDto updateProduct( Long id,
                                             ProductUpdateDto dto,
                                             String email) {
        requireAuthentication(email);

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Product not found"

                        )
                );

        checkVisibility(product, email);

        if (product.getOwner() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Cannot edit a public product"
            );
        }

        double protein = dto.getProtein() != null
                ? dto.getProtein()
                : product.getProtein();

        double fat = dto.getFat() != null
                ? dto.getFat()
                : product.getFat();

        double carbs = dto.getCarbs() != null
                ? dto.getCarbs()
                : product.getCarbs();

        if (protein + fat + carbs > 100.000001) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Total macros cannot exceed 100g"
            );
        }
        if (dto.getBarcode() != null) {

            Optional<Product> existingProduct =
                    productRepository.findByBarcode(dto.getBarcode());

            if (existingProduct.isPresent() &&
                    !existingProduct.get().getId().equals(id)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Product with this barcode already exists"
                );
            }
        }
        if (dto.getName() != null) {
            product.setName(dto.getName().trim());
        }

        if (dto.getBrand() != null) {
            product.setBrand(dto.getBrand());
        }

        if (dto.getCalories() != null) {
            product.setCalories(dto.getCalories());
        }

        if (dto.getProtein() != null) {
            product.setProtein(dto.getProtein());
        }

        if (dto.getFat() != null) {
            product.setFat(dto.getFat());
        }

        if (dto.getCarbs() != null) {
            product.setCarbs(dto.getCarbs());
        }

        if (dto.getBarcode() != null) {
            product.setBarcode(dto.getBarcode());
        }

        return mapToDto(product);
    }

    private ProductResponseDto mapToDto(Product product) {

        Long ownerId = product.getOwner() == null
                ? null
                : product.getOwner().getId();

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .calories(product.getCalories())
                .protein(product.getProtein())
                .fat(product.getFat())
                .carbs(product.getCarbs())
                .barcode(product.getBarcode())
                .ownerId(ownerId)
                .build();
    }


}
