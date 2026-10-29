package com.fittrack_app.fittrack.modules.nutrition.service;

import com.fittrack_app.fittrack.modules.nutrition.dto.FoodEntryRequestDto;
import com.fittrack_app.fittrack.modules.nutrition.dto.FoodEntryResponseDto;
import com.fittrack_app.fittrack.modules.nutrition.entity.FoodEntry;
import com.fittrack_app.fittrack.modules.nutrition.repository.FoodEntryRepository;
import com.fittrack_app.fittrack.modules.producte.entity.Product;
import com.fittrack_app.fittrack.modules.producte.repository.ProductRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodEntryService {
    private final FoodEntryRepository foodEntryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public FoodEntryResponseDto addFood(
            FoodEntryRequestDto dto,
            String email) {

        User user = requireUser(email);

        validateRequest(dto);

        Product product = requireAccessibleProduct(
                dto.getProductId(),
                user
        );

        FoodEntry entry = FoodEntry.builder()
                .user(user)
                .build();

        applyFoodData(entry, dto, product);

        FoodEntry saved = foodEntryRepository.saveAndFlush(entry);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FoodEntryResponseDto> getFoodsByDate(
            LocalDate date,
            String email) {

        User user = requireUser(email);

        if (date == null) {
            throw badRequest("Date is required");
        }

        return foodEntryRepository
                .findByUser_IdAndEntryDateOrderByCreatedAtAsc(
                        user.getId(),
                        date
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FoodEntryResponseDto getFood(
            Long id,
            String email) {

        User user = requireUser(email);

        FoodEntry entry = requireFoodEntry(id, user.getId());

        return mapToDto(entry);
    }

    @Transactional
    public FoodEntryResponseDto updateFood(
            Long id,
            FoodEntryRequestDto dto,
            String email) {

        User user = requireUser(email);

        FoodEntry entry = requireFoodEntry(id, user.getId());

        validateRequest(dto);

        Product product = requireAccessibleProduct(
                dto.getProductId(),
                user
        );

        applyFoodData(entry, dto, product);

        FoodEntry updated = foodEntryRepository.saveAndFlush(entry);

        return mapToDto(updated);
    }

    @Transactional
    public void deleteFood(Long id, String email) {

        User user = requireUser(email);

        FoodEntry entry = requireFoodEntry(id, user.getId());

        foodEntryRepository.delete(entry);
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

    private FoodEntry requireFoodEntry(Long id, Long userId) {

        return foodEntryRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Food entry not found"
                        )
                );
    }

    private Product requireAccessibleProduct(
            Long productId, User user) {

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

        return product;
    }

    private void validateRequest(FoodEntryRequestDto dto) {

        if (dto == null ||
                dto.getProductId() == null ||
                dto.getProductId() <= 0 ||
                dto.getMealType() == null ||
                dto.getEntryDate() == null ||
                dto.getQuantityGrams() == null ||
                !Double.isFinite(dto.getQuantityGrams()) ||
                dto.getQuantityGrams() <= 0) {

            throw badRequest("Invalid food entry data");
        }
    }

    private void applyFoodData(
            FoodEntry entry,
            FoodEntryRequestDto dto,
            Product product) {

        double grams = dto.getQuantityGrams();

        entry.setProductId(product.getId());
        entry.setProductName(product.getName());

        entry.setMealType(dto.getMealType());
        entry.setEntryDate(dto.getEntryDate());
        entry.setQuantityGrams(grams);

        entry.setCalories(calculatePortion(
                product.getCalories(), grams
        ));

        entry.setProteinGrams(calculatePortion(
                product.getProtein(), grams
        ));

        entry.setFatGrams(calculatePortion(
                product.getFat(), grams
        ));

        entry.setCarbGrams(calculatePortion(
                product.getCarbs(), grams
        ));
    }
    private double calculatePortion(
            Double valuePer100g,
            double grams) {

        if (valuePer100g == null ||
                !Double.isFinite(valuePer100g) ||
                valuePer100g < 0) {

            throw badRequest("Invalid product nutrition data");
        }

        double result = valuePer100g * grams / 100.0;

        if (!Double.isFinite(result) ||
                result > Long.MAX_VALUE / 100.0) {
            throw badRequest("Invalid calculated nutrition value");
        }

        return round2(result);
    }

    private FoodEntryResponseDto mapToDto(FoodEntry entry) {

        return FoodEntryResponseDto.builder()
                .id(entry.getId())
                .productId(entry.getProductId())
                .productName(entry.getProductName())
                .mealType(entry.getMealType())
                .entryDate(entry.getEntryDate())
                .quantityGrams(entry.getQuantityGrams())
                .calories(entry.getCalories())
                .proteinGrams(entry.getProteinGrams())
                .fatGrams(entry.getFatGrams())
                .carbGrams(entry.getCarbGrams())
                .createdAt(entry.getCreatedAt())
                .build();
    }


    private double round2(double value) {
            return Math.round(value * 100.0) / 100.0;
        }

        private ResponseStatusException badRequest(String message) {
            return new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }
}
