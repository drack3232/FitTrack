package com.fittrack_app.fittrack.modules.weight.entity;

import com.fittrack_app.fittrack.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;

import java.time.Instant;
import java.time.LocalDate;


@Entity
@Table(
        name = "weight_entries",
        indexes = {
                @Index(
                        name = "idx_weight_entries_user_date",
                        columnList = "user_id, entry_date"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "weight_kg",
            nullable = false
    )
    private Double weightKg;

    @Column(
            name = "entry_date",
            nullable = false
    )
    private LocalDate entryDate;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;
}
