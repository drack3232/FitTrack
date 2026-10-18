package com.fittrack_app.fittrack.modules.producte.service;

import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.entity.FavoriteProduct;
import com.fittrack_app.fittrack.modules.producte.entity.Product;
import com.fittrack_app.fittrack.modules.producte.repository.FavoriteProductRepository;
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

@Service
@RequiredArgsConstructor
public class FavoriteProductService {
    private final FavoriteProductRepository favoriteProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFavorite(Long productId, String email) {

        User user = requireUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );
        if (product.getOwner() != null &&
                !product.getOwner().getId().equals(user.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }

        boolean alreadyExists =
                favoriteProductRepository.existsByUser_IdAndProduct_Id(
                        user.getId(),
                        productId
                );

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product is already in favorites"
            );
        }
        FavoriteProduct favorite = FavoriteProduct.builder()
                .user(user)
                .product(product)
                .build();

        favoriteProductRepository.save(favorite);


    }
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getFavorites(
            String email,
            Pageable pageable) {

        User user = requireUser(email);

        if (pageable.getPageSize() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size cannot exceed 100"
            );
        }

        return favoriteProductRepository.findByUser_Id(
                        user.getId(),
                        pageable
                )
                .map(favorite ->
                        mapToDto(favorite.getProduct())
                );
    }
    @Transactional
    public void removeFavorite(Long productId, String email) {

        User user = requireUser(email);

        FavoriteProduct favorite =
                favoriteProductRepository.findByUser_IdAndProduct_Id(
                        user.getId(),
                        productId
                ).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product is not in favorites"
                        )
                );

        favoriteProductRepository.delete(favorite);
    }

    private User requireUser(String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }

    private ProductResponseDto mapToDto(Product product) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .calories(product.getCalories())
                .protein(product.getProtein())
                .fat(product.getFat())
                .carbs(product.getCarbs())
                .barcode(product.getBarcode())
                .ownerId(
                        product.getOwner() != null
                                ? product.getOwner().getId()
                                : null
                )
                .build();
    }
}
