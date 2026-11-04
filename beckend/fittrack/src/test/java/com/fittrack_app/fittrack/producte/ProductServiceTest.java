
package com.fittrack_app.fittrack.producte;

import com.fittrack_app.fittrack.modules.producte.dto.ProductCreateDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductUpdateDto;
import com.fittrack_app.fittrack.modules.producte.entity.Product;
import com.fittrack_app.fittrack.modules.producte.repository.ProductRepository;

import com.fittrack_app.fittrack.modules.producte.service.ProductService;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User user;
    private Product product;

    private final String email = "test@gmail.com";

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail(email);

        product = Product.builder()
                .id(10L)
                .name("Milk")
                .brand("Test Brand")
                .calories(60.0)
                .protein(3.0)
                .fat(3.0)
                .carbs(5.0)
                .owner(user)
                .build();
    }

    @Test
    void shouldCreateProduct() {

        ProductCreateDto dto = createValidDto();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(10L);
                    return saved;
                });

        ProductResponseDto result =
                productService.createProduct(dto, email);

        assertEquals(10L, result.getId());
        assertEquals("Milk", result.getName());
        assertEquals(60.0, result.getCalories());
        assertEquals(1L, result.getOwnerId());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldRejectMacrosAbove100() {

        ProductCreateDto dto = createValidDto();

        dto.setProtein(50.0);
        dto.setFat(40.0);
        dto.setCarbs(30.0);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.createProduct(dto, email)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldRejectDuplicateBarcode() {

        ProductCreateDto dto = createValidDto();
        dto.setBarcode("123456789");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(productRepository.existsByBarcode("123456789"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.createProduct(dto, email)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldSearchProducts() {

        when(productRepository.searchVisibleProducts(
                eq(email),
                eq("mil"),
                any(PageRequest.class)
        )).thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponseDto> result =
                productService.searchProduct(
                        "mil",
                        email,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Milk",
                result.getContent().get(0).getName()
        );
    }

    @Test
    void shouldFindProductById() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        ProductResponseDto result =
                productService.getProductById(10L, email);

        assertEquals(10L, result.getId());
        assertEquals("Milk", result.getName());
    }

    @Test
    void shouldRejectAccessToAnotherUsersProduct() {

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@gmail.com");

        product.setOwner(otherUser);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getProductById(10L, email)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }

    @Test
    void shouldRejectDeletingPublicProduct() {

        product.setOwner(null);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.deleteProduct(10L, email)
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );

        verify(productRepository, never())
                .delete(any(Product.class));
    }

    @Test
    void shouldDeleteOwnProduct() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(10L, email);

        verify(productRepository).delete(product);
    }

    @Test
    void shouldRejectUnauthorizedCreation() {

        ProductCreateDto dto = createValidDto();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.createProduct(dto, null)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    private ProductCreateDto createValidDto() {

        ProductCreateDto dto = new ProductCreateDto();

        dto.setName("Milk");
        dto.setBrand("Test Brand");
        dto.setCalories(60.0);
        dto.setProtein(3.0);
        dto.setFat(3.0);
        dto.setCarbs(5.0);

        return dto;
    }
}
