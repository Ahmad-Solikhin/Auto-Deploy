package com.gayuh.auto_deploy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BuildHistoryDetailResponse(
        Long id,
        Boolean success,
        String executionTime,
        Long executionTimeEpochUnix,
        LocalDateTime executeAt,
        Long executeAtEpochUnix,
        List<BuildHistoryLogResponse> buildHistoryLogs
) {
}
