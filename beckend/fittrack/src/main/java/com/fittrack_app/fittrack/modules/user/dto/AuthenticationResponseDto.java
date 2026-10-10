package com.fittrack_app.fittrack.modules.user.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder

public class AuthenticationResponseDto {
    private String token;

    public AuthenticationResponseDto(String token) {
        this.token = token;
    }
    public AuthenticationResponseDto() {
    }
}
