package com.fittrack_app.fittrack.modules.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalyticsResponseDto {
    private AnalyticsSummaryDto summary;

    private List<DailyAnalyticsDto> dailyStats;

}
