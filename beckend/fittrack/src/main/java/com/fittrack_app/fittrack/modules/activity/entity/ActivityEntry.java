package com.fittrack_app.fittrack.modules.activity.entity;
import com.fittrack_app.fittrack.modules.activity.enums.ActivityType;
import com.fittrack_app.fittrack.modules.user.entity.User;
import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "activity_entries",
        indexes = {
                @Index(
                        name = "idx_activity_entries_user_date",
                        columnList = "user_id, entry_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "activity_type",
            nullable = false,
            length = 30
    )
    private ActivityType activityType;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "calories_burned", nullable = false)
    private Double caloriesBurned;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
}
