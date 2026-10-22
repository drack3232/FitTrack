package com.fittrack_app.fittrack.modules.goals.entity;

import com.fittrack_app.fittrack.modules.goals.enums.ActivityLevel;
import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import com.fittrack_app.fittrack.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Audited;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Table(name = "user_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "goal_type",
            nullable = false,
            length = 20
    )
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "activity_level",
            nullable = false,
            length = 30
    )
    private ActivityLevel activityLevel;

    @Column(name = "target_weight")
    private Double targetWeight;

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
