package com.fittrack_app.fittrack.modules.producte.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ProductSearchHistoryResponseDto {
    private Long id;

    private String queryText;

    private Instant lastSearchedAt;
}
