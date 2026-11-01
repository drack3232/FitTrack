package com.fittrack_app.fittrack.module.weight.service;

import com.fittrack_app.fittrack.modules.goals.repository.UserGoalRepository;
import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;
import com.fittrack_app.fittrack.modules.weight.dto.WeightEntryResponseDto;
import com.fittrack_app.fittrack.modules.weight.entity.WeightEntry;
import com.fittrack_app.fittrack.modules.weight.repository.WeightEntryRepository;
import com.fittrack_app.fittrack.modules.weight.service.WeightEntryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class WeightEntryServiceTest {
    @Mock
    private WeightEntryRepository weightEntryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGoalRepository userGoalRepository;

    @InjectMocks
    private WeightEntryService weightEntryService;

    @Test
    void shouldReturnWeightEntry(){
        User user = new User();
        user.setId(1L);

        WeightEntry entry = WeightEntry.builder()
                .id(10L)
                .user(user)
                .weightKg(80.5)
                .entryDate(LocalDate.of(2026, 11, 1))
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(weightEntryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(entry));

        WeightEntryResponseDto result =
                weightEntryService.getWeightEntry(
                        10L,
                        "test@gmail.com"
                );

        assertEquals(10L, result.getId());
        assertEquals(80.5, result.getWeightKg());
    }
}
