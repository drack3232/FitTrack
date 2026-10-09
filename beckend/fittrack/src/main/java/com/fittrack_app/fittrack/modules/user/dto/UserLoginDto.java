package com.fittrack_app.fittrack.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class UserLoginDto {
    @NotBlank(message = "Email can`t be empty")
    @Email(message = "Incorrect email, repeat again")
    private String email;

    @NotBlank(,message = "Password can`t be empty")
    private String password;
}
