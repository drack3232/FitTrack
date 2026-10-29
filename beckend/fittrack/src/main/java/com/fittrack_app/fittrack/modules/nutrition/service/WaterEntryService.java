package com.fittrack_app.fittrack.modules.nutrition.service;

import com.fittrack_app.fittrack.modules.nutrition.dto.WaterEntryRequestDto;
import com.fittrack_app.fittrack.modules.nutrition.dto.WaterEntryResponseDto;
import com.fittrack_app.fittrack.modules.nutrition.entity.WaterEntry;
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
public class WaterEntryService {

    private final WaterEntryRepository waterEntryRepository;
    private final UserRepository userRepository;

    @Transactional
    public WaterEntryResponseDto addWater(WaterEntryRequestDto dto, String email){
        User user = requireUser(email);

        validateRequest(dto);

        WaterEntry entry = WaterEntry.builder()
                .user(user)
                .amountMl(dto.getAmountMl())
                .entryDate(dto.getEntryDate())
                .build();

        WaterEntry saved =
                waterEntryRepository.saveAndFlush(entry);

        return mapToDto(saved);


    }

    @Transactional(readOnly = true)
    public List<WaterEntryResponseDto> getWaterByDate(
            LocalDate date,
            String email) {

        User user = requireUser(email);

        if (date == null) {
            throw badRequest("Date is required");
        }

        return waterEntryRepository
                .findByUser_IdAndEntryDateOrderByCreatedAtAsc(
                        user.getId(),
                        date
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long getTotalWater(
            LocalDate date,
            String email) {

        User user = requireUser(email);

        if (date == null) {
            throw badRequest("Date is required");
        }

        return waterEntryRepository.sumWaterByUserAndDate(
                user.getId(),
                date
        );
    }
    @Transactional
    public WaterEntryResponseDto updateWater(
            Long id,
            WaterEntryRequestDto dto,
            String email) {

        User user = requireUser(email);

        WaterEntry entry = requireWaterEntry(
                id,
                user.getId()
        );

        validateRequest(dto);

        entry.setAmountMl(dto.getAmountMl());
        entry.setEntryDate(dto.getEntryDate());

        WaterEntry updated =
                waterEntryRepository.saveAndFlush(entry);

        return mapToDto(updated);
    }

    @Transactional
    public void deleteWater(
            Long id,
            String email) {

        User user = requireUser(email);

        WaterEntry entry = requireWaterEntry(
                id,
                user.getId()
        );

        waterEntryRepository.delete(entry);
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

    private WaterEntry requireWaterEntry(
            Long id,
            Long userId) {

        return waterEntryRepository
                .findByIdAndUser_Id(id, userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Water entry not found"
                        )
                );
    }

    private void validateRequest(WaterEntryRequestDto dto) {

        if (dto == null ||
                dto.getAmountMl() == null ||
                dto.getAmountMl() <= 0 ||
                dto.getEntryDate() == null) {

            throw badRequest("Invalid water entry data");
        }
    }

    private WaterEntryResponseDto mapToDto(
            WaterEntry entry) {

        return WaterEntryResponseDto.builder()
                .id(entry.getId())
                .amountMl(entry.getAmountMl())
                .entryDate(entry.getEntryDate())
                .createdAt(entry.getCreatedAt())
                .build();
    }

    private ResponseStatusException badRequest(String message) {

        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
