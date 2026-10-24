package com.fittrack_app.fittrack.modules.goals.service;

import com.fittrack_app.fittrack.modules.goals.dto.CalorieCalculationResponseDto;
import com.fittrack_app.fittrack.modules.goals.dto.UserGoalRequestDto;
import com.fittrack_app.fittrack.modules.goals.dto.UserGoalResponseDto;
import com.fittrack_app.fittrack.modules.goals.entity.UserGoal;
import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import com.fittrack_app.fittrack.modules.goals.repository.UserGoalRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserGoalService {
    private final UserGoalRepository goalRepository;
    private final UserRepository userRepository;
    private final CalorieCalculationService calculationService;

    @Transactional
    public UserGoalResponseDto saveGoal(
            UserGoalRequestDto dto,
            String email) {

        User user = requireUser(email);

        validateGoalRequest(dto, user);

        UserGoal goal = goalRepository.findByUser_Id(user.getId())
                .orElseGet(() ->
                        UserGoal.builder()
                                .user(user)
                                .build()
                );

        goal.setGoalType(dto.getGoalType());
        goal.setActivityLevel(dto.getActivityLevel());
        goal.setTargetWeight(dto.getTargetWeight());

        UserGoal savedGoal = goalRepository.saveAndFlush(goal);

        return mapToDto(savedGoal);
    }


    @Transactional(readOnly = true)
    public UserGoalResponseDto getGoal(String email) {

        User user = requireUser(email);

        UserGoal goal = requireGoal(user.getId());

        return mapToDto(goal);
    }

    @Transactional(readOnly = true)
    public CalorieCalculationResponseDto getCalculations(
            String email) {

        User user = requireUser(email);

        UserGoal goal = requireGoal(user.getId());

        return calculationService.calculate(user, goal);
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

    private UserGoal requireGoal(Long userId) {

        return goalRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User goal not found"
                        )
                );
    }

    private void validateGoalRequest(
            UserGoalRequestDto dto,
            User user) {

        if (dto == null ||
                dto.getGoalType() == null ||
                dto.getActivityLevel() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Goal type and activity level are required"
            );
        }

        Double targetWeight = dto.getTargetWeight();

        if (targetWeight != null &&
                (!Double.isFinite(targetWeight) ||
                        targetWeight <= 0 ||
                        targetWeight > 500)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid target weight"
            );
        }

        GoalType goalType = dto.getGoalType();

        if (goalType == GoalType.LOSE ||
                goalType == GoalType.GAIN) {

            if (targetWeight == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Target weight is required"
                );
            }

            Double currentWeight = user.getWeight();

            if (currentWeight == null ||
                    !Double.isFinite(currentWeight) ||
                    currentWeight <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Set a valid current weight in your profile"
                );
            }

            if (goalType == GoalType.LOSE &&
                    targetWeight >= currentWeight) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Target weight must be below current weight"
                );
            }

            if (goalType == GoalType.GAIN &&
                    targetWeight <= currentWeight) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Target weight must be above current weight"
                );
            }
        }
    }

    private UserGoalResponseDto mapToDto(UserGoal goal) {

        return UserGoalResponseDto.builder()
                .id(goal.getId())
                .goalType(goal.getGoalType())
                .activityLevel(goal.getActivityLevel())
                .targetWeight(goal.getTargetWeight())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}
