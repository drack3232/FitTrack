package com.fittrack_app.fittrack.modules.activity.service;

import com.fittrack_app.fittrack.modules.activity.dto.ActivityRequestDto;
import com.fittrack_app.fittrack.modules.activity.dto.ActivityResponseDto;
import com.fittrack_app.fittrack.modules.activity.dto.DailyActivitySummaryDto;
import com.fittrack_app.fittrack.modules.activity.entity.ActivityEntry;
import com.fittrack_app.fittrack.modules.activity.repository.ActivityRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivityResponseDto addActivity(
            ActivityRequestDto dto,
            String email) {

        User user = requireUser(email);

        validateRequest(dto);

        ActivityEntry activity = ActivityEntry.builder()
                .user(user)
                .activityType(dto.getActivityType())
                .entryDate(dto.getEntryDate())
                .durationMinutes(dto.getDurationMinutes())
                .caloriesBurned(dto.getCaloriesBurned())
                .build();

        ActivityEntry saved =
                activityRepository.saveAndFlush(activity);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDto> getActivitiesByDate(
            LocalDate date,
            String email) {

        User user = requireUser(email);

        if (date == null) {
            throw badRequest("Date is required");
        }

        return activityRepository
                .findByUser_IdAndEntryDateOrderByCreatedAtAsc(
                        user.getId(),
                        date
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponseDto getActivity(
            Long id,
            String email) {

        User user = requireUser(email);

        ActivityEntry activity =
                requireActivity(id, user.getId());

        return mapToDto(activity);
    }

    @Transactional
    public ActivityResponseDto updateActivity(
            Long id,
            ActivityRequestDto dto,
            String email) {

        User user = requireUser(email);

        ActivityEntry activity =
                requireActivity(id, user.getId());

        validateRequest(dto);

        activity.setActivityType(dto.getActivityType());
        activity.setEntryDate(dto.getEntryDate());
        activity.setDurationMinutes(dto.getDurationMinutes());
        activity.setCaloriesBurned(dto.getCaloriesBurned());

        ActivityEntry updated =
                activityRepository.saveAndFlush(activity);

        return mapToDto(updated);
    }


    @Transactional
    public void deleteActivity(
            Long id,
            String email) {

        User user = requireUser(email);

        ActivityEntry activity =
                requireActivity(id, user.getId());

        activityRepository.delete(activity);
    }


    @Transactional(readOnly = true)
    public DailyActivitySummaryDto getDailySummary(LocalDate date, String email) {

        User user = requireUser(email);

        if (date == null) {
            throw badRequest("Date is required");
        }

        List<ActivityEntry> activities = activityRepository
                .findByUser_IdAndEntryDateOrderByCreatedAtAsc(
                        user.getId(),
                        date
                );

        long totalActivities = activities.size();

        long totalDuration = activities.stream()
                .mapToLong(ActivityEntry::getDurationMinutes)
                .sum();

        double totalCalories = activities.stream()
                .mapToDouble(ActivityEntry::getCaloriesBurned)
                .sum();

        return DailyActivitySummaryDto.builder()
                .entryDate(date)
                .totalActivities(totalActivities)
                .totalDurationMinutes(totalDuration)
                .totalCaloriesBurned(round2(totalCalories))
                .build();
    }

    private ActivityEntry requireActivity(
            Long id,
            Long userId) {

        return activityRepository
                .findByIdAndUser_Id(id, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Activity not found"
                        )
                );
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
    private void validateRequest(ActivityRequestDto dto) {

        if (dto == null ||
                dto.getActivityType() == null ||
                dto.getEntryDate() == null ||
                dto.getDurationMinutes() == null ||
                dto.getDurationMinutes() <= 0 ||
                dto.getCaloriesBurned() == null ||
                !Double.isFinite(dto.getCaloriesBurned()) ||
                dto.getCaloriesBurned() < 0) {

            throw badRequest("Invalid activity data");
        }
    }

    private ActivityResponseDto mapToDto(
            ActivityEntry activity) {

        return ActivityResponseDto.builder()
                .id(activity.getId())
                .activityType(activity.getActivityType())
                .entryDate(activity.getEntryDate())
                .durationMinutes(activity.getDurationMinutes())
                .caloriesBurned(activity.getCaloriesBurned())
                .createdAt(activity.getCreatedAt())
                .build();
    }
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ResponseStatusException badRequest(
            String message) {

        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }


}
