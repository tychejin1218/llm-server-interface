package com.wanted.assignment.domain.repository;

import com.wanted.assignment.domain.entity.LlmUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlmUsageRepository extends
    JpaRepository<LlmUsageEntity, Long> {

}
