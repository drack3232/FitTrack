
package com.fittrack_app.fittrack.modules.user.dto;

public record UserProfileDto(
        Long id,
        String email,
        String gender,
        Integer age,
        Double height,
        Double weight
) {
}
