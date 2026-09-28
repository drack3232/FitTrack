package com.fittrack_app.fittrack.modules.user.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "Email is necessary")
    @Email(message = "Email isn`t correct")
    private String email;
    @NotBlank(message = "Password is necessary")
    @Size(min = 6, message = "Password must have 6 symbol")
    private String password;

    private String gender;

    @Positive(message = "Age must be more than 0")
    private Integer age;

    @Positive(message = "Weight must be than 0")
    private Double weight;

    @Positive(message = "Height must be than 0")
    private Double height;
}
