package com.wanted.assignment.llm.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wanted.assignment.domain.repository.LlmUsageRepository;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class LlmUsageQueryRepositoryTest {

  private static final long LLM_ID = 3L;

  @Autowired
  LlmUsageQueryRepository llmUsageQueryRepository;

  @Autowired
  LlmUsageRepository llmUsageRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteLlmUsage - LLM 사용량 삭제")
  @Nested
  class TestDeleteLlmUsage {

    @Order(1)
    @DisplayName("LLM 사용량 삭제 성공")
    @Transactional
    @Test
    void testDeleteLlmUsageSuccess() {

      // Given & When
      boolean isDeleted = llmUsageQueryRepository.deleteLlmUsageByLlmId(LLM_ID);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isDeleted),
          () -> assertTrue(llmUsageRepository.findById(LLM_ID).get().getIsDeleted())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectLlmUsageStats - LLM별 사용량 조회  조회")
  @Nested
  class TestSelectLlmUsageStats {

    @Order(1)
    @DisplayName("LLM별 사용량 조회  성공")
    @Transactional
    @Test
    void testSelectLlmUsageStatsSuccess() {

      // Given & When
      List<LlmUsageDto.StatsResponse> statsResponses =
          llmUsageQueryRepository.selectLlmUsageStats();

      // Then
      assertFalse(statsResponses.isEmpty());
    }
  }

  @Disabled
  void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }
}
