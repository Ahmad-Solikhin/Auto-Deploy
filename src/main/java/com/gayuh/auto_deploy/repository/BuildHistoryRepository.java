package com.gayuh.auto_deploy.repository;

import com.gayuh.auto_deploy.entity.BuildHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildHistoryRepository extends JpaRepository<BuildHistory, Long> {
}
