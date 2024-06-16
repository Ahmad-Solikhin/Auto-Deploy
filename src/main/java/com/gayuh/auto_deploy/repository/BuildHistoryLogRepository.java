package com.gayuh.auto_deploy.repository;

import com.gayuh.auto_deploy.entity.BuildHistoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildHistoryLogRepository extends JpaRepository<BuildHistoryLog, Long> {
}
