package com.gayuh.auto_deploy.dto;

public record ProjectBuildHistoryQuery(
        String projectId,
        String name,
        String language,
        String description,
        Long buildHistoryId,
        Boolean success,
        Long executionTime,
        Long executeAt
) {
}
