
package com.fittrack_app.fittrack.modules.producte.service;

import com.fittrack_app.fittrack.modules.producte.dto.ProductSearchHistoryResponseDto;
import com.fittrack_app.fittrack.modules.producte.entity.ProductSearchHistory;
import com.fittrack_app.fittrack.modules.producte.repository.ProductSearchHistoryRepository;

import com.fittrack_app.fittrack.modules.user.entity.User;
import com.fittrack_app.fittrack.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductSearchHistoryService {

    private final ProductSearchHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void recordSearch(String query, String email) {

        if (email == null || email.isBlank()) {
            return;
        }

        User user = requireUser(email);

        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search query cannot be blank"
            );
        }

        String queryText = query.strip()
                .replaceAll("\\s+", " ");

        String normalizedQuery =
                queryText.toLowerCase(Locale.ROOT);

        if (queryText.length() > 200 ||
                normalizedQuery.length() > 200) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search query cannot exceed 200 characters"
            );
        }

        historyRepository.findByUser_IdAndNormalizedQuery(
                user.getId(),
                normalizedQuery
        ).ifPresentOrElse(


                history -> {
                    history.setQueryText(queryText);
                    history.setLastSearchedAt(Instant.now());
                },

                () -> {
                    ProductSearchHistory history =
                            ProductSearchHistory.builder()
                                    .user(user)
                                    .queryText(queryText)
                                    .normalizedQuery(normalizedQuery)
                                    .lastSearchedAt(Instant.now())
                                    .build();

                    historyRepository.save(history);
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductSearchHistoryResponseDto> getHistory(
            String email,
            Pageable pageable) {

        User user = requireUser(email);

        if (pageable.isUnpaged() ||
                pageable.getPageSize() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size cannot exceed 100"
            );
        }

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Direction.DESC,
                        "lastSearchedAt"
                )
        );

        return historyRepository.findByUser_Id(
                user.getId(),
                sortedPageable
        ).map(this::mapToDto);
    }

    @Transactional
    public void deleteHistoryEntry(Long id, String email) {

        User user = requireUser(email);

        ProductSearchHistory history =
                historyRepository.findByIdAndUser_Id(
                        id,
                        user.getId()
                ).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Search history entry not found"
                        )
                );

        historyRepository.delete(history);
    }

    @Transactional
    public int clearHistory(String email) {

        User user = requireUser(email);

        return historyRepository.deleteAllForUser(
                user.getId()
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

    private ProductSearchHistoryResponseDto mapToDto(
            ProductSearchHistory history) {

        return ProductSearchHistoryResponseDto.builder()
                .id(history.getId())
                .queryText(history.getQueryText())
                .lastSearchedAt(history.getLastSearchedAt())
                .build();
    }
}
