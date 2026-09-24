package com.fittrack_app.fittrack.modules.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String gender;
    private Integer age;
    private Double height;
    private Double weight;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

