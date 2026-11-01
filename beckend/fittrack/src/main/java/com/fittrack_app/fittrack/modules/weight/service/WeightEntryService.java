
package com.fittrack_app.fittrack.modules.weight.service;

import com.fittrack_app.fittrack.modules.weight.dto.WeightEntryRequestDto;
import com.fittrack_app.fittrack.modules.weight.dto.WeightEntryResponseDto;
import com.fittrack_app.fittrack.modules.weight.dto.WeightProgressDto;
import com.fittrack_app.fittrack.modules.weight.entity.WeightEntry;
import com.fittrack_app.fittrack.modules.weight.repository.WeightEntryRepository;

import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;

import com.fittrack_app.fittrack.modules.goals.entity.UserGoal;
import com.fittrack_app.fittrack.modules.goals.enums.GoalType;
import com.fittrack_app.fittrack.modules.goals.repository.UserGoalRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeightEntryService {

    private final WeightEntryRepository weightEntryRepository;
    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;

    @Transactional
    public WeightEntryResponseDto addWeight(
            WeightEntryRequestDto dto,
            String email) {

        User user = requireUser(email);
        validateRequest(dto);

        WeightEntry entry = WeightEntry.builder()
                .user(user)
                .weightKg(dto.getWeightKg())
                .entryDate(dto.getEntryDate())
                .build();

        WeightEntry saved = weightEntryRepository.saveAndFlush(entry);

        if (isLatestEntry(saved.getId(), user.getId())) {
            syncCurrentWeight(user);
        }

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WeightEntryResponseDto> getWeightHistory(String email) {

        User user = requireUser(email);

        return weightEntryRepository
                .findByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WeightEntryResponseDto> getWeightHistoryByPeriod(
            LocalDate startDate,
            LocalDate endDate,
            String email) {

        User user = requireUser(email);

        if (startDate == null || endDate == null ||
                startDate.isAfter(endDate)) {
            throw badRequest("Invalid date range");
        }

        return weightEntryRepository
                .findByUser_IdAndEntryDateBetweenOrderByEntryDateAscCreatedAtAscIdAsc(
                        user.getId(),
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public WeightEntryResponseDto getWeightEntry(
            Long id,
            String email) {

        User user = requireUser(email);

        WeightEntry entry = requireWeightEntry(id, user.getId());

        return mapToDto(entry);
    }

    @Transactional
    public WeightEntryResponseDto updateWeight(
            Long id,
            WeightEntryRequestDto dto,
            String email) {

        User user = requireUser(email);
        WeightEntry entry = requireWeightEntry(id, user.getId());

        validateRequest(dto);

        boolean wasLatest = isLatestEntry(id, user.getId());

        entry.setWeightKg(dto.getWeightKg());
        entry.setEntryDate(dto.getEntryDate());

        WeightEntry updated = weightEntryRepository.saveAndFlush(entry);

        if (wasLatest || isLatestEntry(id, user.getId())) {
            syncCurrentWeight(user);
        }

        return mapToDto(updated);
    }


    @Transactional
    public void deleteWeight(Long id, String email) {

        User user = requireUser(email);
        WeightEntry entry = requireWeightEntry(id, user.getId());

        boolean wasLatest = isLatestEntry(id, user.getId());

        weightEntryRepository.delete(entry);
        weightEntryRepository.flush();

        if (wasLatest) {
            syncCurrentWeight(user);
        }
    }

    @Transactional(readOnly = true)
    public WeightProgressDto getProgress(String email) {

        User user = requireUser(email);

        WeightEntry first = weightEntryRepository
                .findFirstByUser_IdOrderByEntryDateAscCreatedAtAscIdAsc(
                        user.getId()
                )
                .orElse(null);

        WeightEntry latest = weightEntryRepository
                .findFirstByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
                        user.getId()
                )
                .orElse(null);

        UserGoal goal = userGoalRepository
                .findByUser_Id(user.getId())
                .orElse(null);

        Double startingWeight = first != null
                ? first.getWeightKg()
                : null;

        Double currentWeight = latest != null
                ? latest.getWeightKg()
                : user.getWeight();

        Double targetWeight = goal != null
                ? goal.getTargetWeight()
                : null;

        GoalType goalType = goal != null
                ? goal.getGoalType()
                : null;

        Double weightChange = null;
        Double remaining = null;
        Double progressPercent = null;

        if (startingWeight != null && currentWeight != null) {
            weightChange = round2(currentWeight - startingWeight);
        }

        if (targetWeight != null && currentWeight != null) {
            remaining = round2(
                    Math.abs(targetWeight - currentWeight)
            );
        }

        if (startingWeight != null &&
                currentWeight != null &&
                targetWeight != null &&
                goalType != null &&
                goalType != GoalType.MAINTAIN) {

            double totalDistance = targetWeight - startingWeight;

            if (Math.abs(totalDistance) < 0.000001) {
                progressPercent =
                        Math.abs(currentWeight - targetWeight) < 0.000001
                                ? 100.0
                                : 0.0;
            } else {
                double progress =
                        (currentWeight - startingWeight)
                                / totalDistance * 100.0;

                progressPercent = round2(
                        Math.max(0.0, Math.min(100.0, progress))
                );
            }
        }

        return WeightProgressDto.builder()
                .goalType(goalType)
                .startingWeightKg(startingWeight)
                .currentWeightKg(currentWeight)
                .targetWeightKg(targetWeight)
                .weightChangeKg(weightChange)
                .remainingKg(remaining)
                .progressPercent(progressPercent)
                .firstMeasurementDate(
                        first != null ? first.getEntryDate() : null
                )
                .lastMeasurementDate(
                        latest != null ? latest.getEntryDate() : null
                )
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

    private WeightEntry requireWeightEntry(Long id, Long userId) {

        return weightEntryRepository
                .findByIdAndUser_Id(id, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Weight entry not found"
                        )
                );
    }


    private void validateRequest(WeightEntryRequestDto dto) {

        if (dto == null ||
                dto.getWeightKg() == null ||
                !Double.isFinite(dto.getWeightKg()) ||
                dto.getWeightKg() <= 0 ||
                dto.getEntryDate() == null) {

            throw badRequest("Invalid weight data");
        }
    }

    private boolean isLatestEntry(Long entryId, Long userId) {

        return weightEntryRepository
                .findFirstByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
                        userId
                )
                .map(entry -> entry.getId().equals(entryId))
                .orElse(false);
    }

    private void syncCurrentWeight(User user) {

        weightEntryRepository
                .findFirstByUser_IdOrderByEntryDateDescCreatedAtDescIdDesc(
                        user.getId()
                )
                .ifPresent(latest -> {
                    user.setWeight(latest.getWeightKg());
                    userRepository.save(user);
                });
    }

    private WeightEntryResponseDto mapToDto(WeightEntry entry) {

        return WeightEntryResponseDto.builder()
                .id(entry.getId())
                .weightKg(entry.getWeightKg())
                .entryDate(entry.getEntryDate())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ResponseStatusException badRequest(String message) {

        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
