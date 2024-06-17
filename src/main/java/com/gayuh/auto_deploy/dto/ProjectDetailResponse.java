package com.gayuh.auto_deploy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProjectDetailResponse(
        String id,
        String name,
        String language,
        String description,
        LocalDateTime lastBuild,
        Long lastBuildEpochUnix,
        String buildSummary,
        List<BuildHistoryResponse> buildHistories
) {
}
