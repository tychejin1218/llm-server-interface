package com.wanted.assignment.domain.repository;

import com.wanted.assignment.domain.entity.LlmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlmRepository extends
    JpaRepository<LlmEntity, Long> {

  /**
   * 이름을 기준으로 LLM이 존재하는지 확인
   *
   * @param name 확인할 LLM 이름
   * @return LLM이 존재하면 true, 그렇지 않으면 false
   */
  boolean existsByName(String name);
}
