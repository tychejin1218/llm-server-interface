package com.wanted.assignment.llm.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wanted.assignment.llm.dto.LlmUsageDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
class LlmUsageServiceTest {

  private static final long USER_ID = 1L;
  private static final long LLM_ID = 1L;
  private static final int USED_TOKEN = 512;

  @Autowired
  LlmUsageService llmUsageService;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertLlm - LLM 기록")
  @Nested
  class TestInsertLlm {

    @Order(1)
    @DisplayName("LLM 기록 성공")
    @Transactional
    @Test
    void testInsertLlmUsageSuccess() {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, LLM_ID, USED_TOKEN
      );

      // When
      long llmUsageId = llmUsageService.insertLlmUsage(insertRequest);

      // Then
      assertAll(
          () -> assertNotNull(llmUsageId)
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getLlmUsageStats - LLM별 사용량 조회")
  @Nested
  class TestGetLlmUsageStats {

    @Order(1)
    @DisplayName("LLM별 사용량 조회 성공")
    @Transactional
    @Test
    void testGetLlmListSuccess() {

      // Given & When
      List<LlmUsageDto.StatsResponse> statsResponses = llmUsageService.getLlmUsageStats();

      // Then
      assertFalse(statsResponses.isEmpty());
    }
  }
}
