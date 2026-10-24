
package com.fittrack_app.fittrack.modules.nutrition.entity;

import com.fittrack_app.fittrack.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "water_entries",
        indexes = {
                @Index(
                        name = "idx_water_entries_user_date",
                        columnList = "user_id, entry_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount_ml", nullable = false)
    private Integer amountMl;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
}
