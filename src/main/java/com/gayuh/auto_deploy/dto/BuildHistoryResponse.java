package com.gayuh.auto_deploy.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BuildHistoryResponse(
        Long id,
        Boolean success,
        String executionTime,
        Long executionTimeEpochUnix,
        LocalDateTime executeAt,
        Long executeAtEpochUnix
) {
}
