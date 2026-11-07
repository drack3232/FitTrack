
package com.fittrack_app.fittrack.modules.analytics.service;

import com.fittrack_app.fittrack.modules.analytics.dto.AnalyticsResponseDto;
import com.fittrack_app.fittrack.modules.analytics.dto.AnalyticsSummaryDto;
import com.fittrack_app.fittrack.modules.analytics.dto.DailyAnalyticsDto;

import com.fittrack_app.fittrack.modules.nutrition.entity.FoodEntry;
import com.fittrack_app.fittrack.modules.nutrition.entity.WaterEntry;
import com.fittrack_app.fittrack.modules.nutrition.repository.FoodEntryRepository;
import com.fittrack_app.fittrack.modules.nutrition.repository.WaterEntryRepository;

import com.fittrack_app.fittrack.modules.activity.entity.ActivityEntry;
import com.fittrack_app.fittrack.modules.activity.repository.ActivityRepository;

import com.fittrack_app.fittrack.modules.weight.entity.WeightEntry;
import com.fittrack_app.fittrack.modules.weight.repository.WeightEntryRepository;

import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final long MAX_DAYS = 90;

    private final FoodEntryRepository foodEntryRepository;
    private final WaterEntryRepository waterEntryRepository;
    private final ActivityRepository activityRepository;
    private final WeightEntryRepository weightEntryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AnalyticsResponseDto getAnalytics(
            LocalDate startDate,
            LocalDate endDate,
            String email) {

        validateDates(startDate, endDate);

        User user = requireUser(email);
        Long userId = user.getId();


        List<FoodEntry> foods = foodEntryRepository
                .findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAsc(
                        userId, startDate, endDate
                );

        List<WaterEntry> waters = waterEntryRepository
                .findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAsc(
                        userId, startDate, endDate
                );

        List<ActivityEntry> activities = activityRepository
                .findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAsc(
                        userId, startDate, endDate
                );

        List<WeightEntry> weights = weightEntryRepository
                .findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAscIdAsc(
                        userId, startDate, endDate
                );



        Map<LocalDate, List<FoodEntry>> foodsByDate =
                foods.stream().collect(
                        Collectors.groupingBy(FoodEntry::getEntryDate)
                );

        Map<LocalDate, List<WaterEntry>> watersByDate =
                waters.stream().collect(
                        Collectors.groupingBy(WaterEntry::getEntryDate)
                );

        Map<LocalDate, List<ActivityEntry>> activitiesByDate =
                activities.stream().collect(
                        Collectors.groupingBy(ActivityEntry::getEntryDate)
                );


        Map<LocalDate, Double> weightsByDate = new HashMap<>();

        for (WeightEntry weight : weights) {
            weightsByDate.put(
                    weight.getEntryDate(),
                    weight.getWeightKg()
            );
        }


        long daysCount =
                ChronoUnit.DAYS.between(startDate, endDate) + 1;

        List<DailyAnalyticsDto> dailyStats = new ArrayList<>();

        for (int i = 0; i < daysCount; i++) {

            LocalDate date = startDate.plusDays(i);

            List<FoodEntry> dayFoods =
                    foodsByDate.getOrDefault(date, List.of());

            List<WaterEntry> dayWaters =
                    watersByDate.getOrDefault(date, List.of());

            List<ActivityEntry> dayActivities =
                    activitiesByDate.getOrDefault(date, List.of());

            double caloriesConsumed = dayFoods.stream()
                    .mapToDouble(FoodEntry::getCalories)
                    .sum();

            double protein = dayFoods.stream()
                    .mapToDouble(FoodEntry::getProteinGrams)
                    .sum();

            double fat = dayFoods.stream()
                    .mapToDouble(FoodEntry::getFatGrams)
                    .sum();

            double carbs = dayFoods.stream()
                    .mapToDouble(FoodEntry::getCarbGrams)
                    .sum();

            long waterMl = dayWaters.stream()
                    .mapToLong(WaterEntry::getAmountMl)
                    .sum();

            long activityCount = dayActivities.size();

            long activityMinutes = dayActivities.stream()
                    .mapToLong(ActivityEntry::getDurationMinutes)
                    .sum();

            double caloriesBurned = dayActivities.stream()
                    .mapToDouble(ActivityEntry::getCaloriesBurned)
                    .sum();

            DailyAnalyticsDto daily = DailyAnalyticsDto.builder()
                    .date(date)
                    .caloriesConsumed(round2(caloriesConsumed))
                    .proteinGrams(round2(protein))
                    .fatGrams(round2(fat))
                    .carbsGrams(round2(carbs))
                    .waterMl(waterMl)
                    .activityCount(activityCount)
                    .activityMinutes(activityMinutes)
                    .caloriesBurned(round2(caloriesBurned))
                    .weightKg(weightsByDate.get(date))
                    .build();

            dailyStats.add(daily);
        }



        double totalCalories = dailyStats.stream()
                .mapToDouble(DailyAnalyticsDto::getCaloriesConsumed)
                .sum();

        double totalProtein = dailyStats.stream()
                .mapToDouble(DailyAnalyticsDto::getProteinGrams)
                .sum();

        double totalFat = dailyStats.stream()
                .mapToDouble(DailyAnalyticsDto::getFatGrams)
                .sum();

        double totalCarbs = dailyStats.stream()
                .mapToDouble(DailyAnalyticsDto::getCarbsGrams)
                .sum();

        long totalWater = dailyStats.stream()
                .mapToLong(DailyAnalyticsDto::getWaterMl)
                .sum();

        long totalActivities = dailyStats.stream()
                .mapToLong(DailyAnalyticsDto::getActivityCount)
                .sum();

        long totalActivityMinutes = dailyStats.stream()
                .mapToLong(DailyAnalyticsDto::getActivityMinutes)
                .sum();

        double totalCaloriesBurned = dailyStats.stream()
                .mapToDouble(DailyAnalyticsDto::getCaloriesBurned)
                .sum();



        Double startingWeight = null;
        Double endingWeight = null;
        Double weightChange = null;

        if (!weights.isEmpty()) {

            startingWeight = weights.get(0).getWeightKg();

            endingWeight = weights
                    .get(weights.size() - 1)
                    .getWeightKg();

            weightChange = round2(
                    endingWeight - startingWeight
            );
        }



        AnalyticsSummaryDto summary = AnalyticsSummaryDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalCaloriesConsumed(round2(totalCalories))
                .averageDailyCalories(
                        round2(totalCalories / daysCount)
                )
                .totalProteinGrams(round2(totalProtein))
                .totalFatGrams(round2(totalFat))
                .totalCarbsGrams(round2(totalCarbs))
                .totalWaterMl(totalWater)
                .totalActivities(totalActivities)
                .totalActivityMinutes(totalActivityMinutes)
                .totalCaloriesBurned(round2(totalCaloriesBurned))
                .startingWeightKg(startingWeight)
                .endingWeightKg(endingWeight)
                .weightChangeKg(weightChange)
                .build();



        return AnalyticsResponseDto.builder()
                .summary(summary)
                .dailyStats(dailyStats)
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

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start and end dates are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date cannot be after end date"
            );
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (days > MAX_DAYS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum period is 90 days"
            );
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
