package com.fittrack_app.fittrack.modules.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
private Long id;
private String name;
private Double weight;
private Double height;
private Integer age;
private String gender;
private LocalDateTime createdAt;
private String email;
}
