package com.fittrack_app.fittrack.modules.user.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    @Min(value = 1)
    @Max(value = 120)
    private Integer age;

    @DecimalMin(value ="0.0", inclusive = false)
    private Double height;

    @DecimalMin(value ="0.0", inclusive = false)
private Double weight;

    private String gender;
}
