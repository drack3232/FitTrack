package com.fittrack_app.fittrack.modules.nutrition.entity;

import com.fittrack_app.fittrack.modules.nutrition.enums.MealType;
import com.fittrack_app.fittrack.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "food_entries",
        indexes = {
                @Index(
                        name = "idx_food_entries_user_date",
                        columnList = "user_id, entry_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_id", nullable = false)
    private Long productId;


    @Column(name = "product_name", nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "quantity_grams", nullable = false)
    private Double quantityGrams;

    @Column(nullable = false)
    private Double calories;

    @Column(name = "protein_grams", nullable = false)
    private Double proteinGrams;

    @Column(name = "fat_grams", nullable = false)
    private Double fatGrams;

    @Column(name = "carb_grams", nullable = false)
    private Double carbGrams;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
}
