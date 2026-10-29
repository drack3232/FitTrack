package com.fittrack_app.fittrack.modules.nutrition.service;

import com.fittrack_app.fittrack.modules.goals.repository.UserGoalRepository;
import com.fittrack_app.fittrack.modules.goals.service.CalorieCalculationService;
import com.fittrack_app.fittrack.modules.nutrition.dto.DailyNutritionSummaryDto;
import com.fittrack_app.fittrack.modules.nutrition.entity.FoodEntry;
import com.fittrack_app.fittrack.modules.nutrition.repository.FoodEntryRepository;
import com.fittrack_app.fittrack.modules.nutrition.repository.WaterEntryRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyNutritionService {

    private final FoodEntryRepository foodEntryRepository;
    private final WaterEntryRepository waterEntryRepository;

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final CalorieCalculationService calculationService;

    @Transactional(readOnly = true)
    public DailyNutritionSummaryDto getDailySummary(
            LocalDate date,
            String email) {

        if (date == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date is required"
            );
        }

        User user = requireUser(email);

        List<FoodEntry> foods = foodEntryRepository
                .findByUser_IdAndEntryDateOrderByCreatedAtAsc(
                        user.getId(),
                        date
                );

        double totalCalories = foods.stream()
                .mapToDouble(FoodEntry::getCalories)
                .sum();

        double totalProtein = foods.stream()
                .mapToDouble(FoodEntry::getProteinGrams)
                .sum();

        double totalFat = foods.stream()
                .mapToDouble(FoodEntry::getFatGrams)
                .sum();

        double totalCarbs = foods.stream()
                .mapToDouble(FoodEntry::getCarbGrams)
                .sum();

        Long totalWater = waterEntryRepository
                .sumWaterByUserAndDate(
                        user.getId(),
                        date
                );

        Double dailyCalorieTarget = userGoalRepository
                .findByUser_Id(user.getId())
                .map(goal -> calculationService
                        .calculate(user, goal)
                        .getDailyCalorieTarget())
                .orElse(null);

        Double remainingCalories = dailyCalorieTarget ==null
                ? null
                : round2(dailyCalorieTarget - totalCalories);


        return DailyNutritionSummaryDto.builder()
                .entryDate(date)
                .totalCalories(round2(totalCalories))
                .totalProteinGrams(round2(totalProtein))
                .totalFatGrams(round2(totalFat))
                .totalCarbGrams(round2(totalCarbs))
                .totalWaterMl(totalWater)
                .dailyCalorieTarget(dailyCalorieTarget)
                .remainingCalories(remainingCalories)
                .build();
    }

    private User requireUser(String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }


        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    }
