package com.fittrack_app.fittrack.modules.producte.service;

import com.fittrack_app.fittrack.modules.producte.dto.ProductCreateDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.entity.Product;
import com.fittrack_app.fittrack.modules.producte.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

@Transactional
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        if (productRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Product already exist");
        }
        double tatalMacros = dto.getProtein() + dto.getCarbs() + dto.getFat();
        if (tatalMacros > 100.0) {
            throw new IllegalArgumentException("Total macros can`t exceed 100 g");
        }

        Product product = Product.builder()
                .name(dto.getName())
                .calories(dto.getCalories())
                .brand(dto.getBrand())
                .carbs(dto.getCarbs())
                .protein(dto.getProtein())
                .fat(dto.getFat())
                .build();

        Product saveProduct = productRepository.save(product);
        return mapToDto(saveProduct);
    }
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProduct (String query){
    return productRepository.findByNameContainingIgnoreCase(query)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id){
    Product product = productRepository.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("Product by ID: " + id + "not found" ));

            return mapToDto(product);
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
                .build();
    }

}
