package com.fittrack_app.fittrack.modules.goals.service;

import com.fittrack_app.fittrack.modules.goals.dto.CalorieCalculationResponseDto;
import com.fittrack_app.fittrack.modules.goals.entity.UserGoal;
import com.fittrack_app.fittrack.modules.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class CalorieCalculationService {
    private static final double CALORIE_ADJUSTMENT = 300.0;

    public CalorieCalculationResponseDto calculate(
            User user,
            UserGoal goal) {

        if (user == null || goal == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User and goal are required"
            );
        }

        Double weight = user.getWeight();
        Double height = user.getHeight();
        Integer age = user.getAge();
        String gender = user.getGender();

        if (weight == null || height == null ||
                age == null || gender == null ||
                !Double.isFinite(weight) ||
                !Double.isFinite(height) ||
                weight <= 0 || height <= 0 || age <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Complete your profile before calculation"
            );
        }

        if (goal.getGoalType() == null ||
                goal.getActivityLevel() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Goal type and activity level are required"
            );
        }

        double heightMeters = height / 100.0;

        double bmi = weight /
                (heightMeters * heightMeters);

        double baseBmr = 10.0 * weight
                + 6.25 * height
                - 5.0 * age;

        double bmr = switch (
                gender.trim().toUpperCase(Locale.ROOT)
                ) {
            case "MALE" -> baseBmr + 5.0;
            case "FEMALE" -> baseBmr - 161.0;

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported gender value"
            );
        };

        if (bmr <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot calculate BMR from profile data"
            );
        }

        double tdee = bmr *
                goal.getActivityLevel().getMultiplier();

        double dailyCalorieTarget = switch (
                goal.getGoalType()
                ) {
            case LOSE -> tdee - CALORIE_ADJUSTMENT;
            case MAINTAIN -> tdee;
            case GAIN -> tdee + CALORIE_ADJUSTMENT;
        };

        if (dailyCalorieTarget <= 0 ||
                !Double.isFinite(dailyCalorieTarget)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid calorie target"
            );
        }

        return CalorieCalculationResponseDto.builder()
                .bmi(round2(bmi))
                .bmr(round2(bmr))
                .tdee(round2(tdee))
                .dailyCalorieTarget(round2(dailyCalorieTarget))
                .build();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
