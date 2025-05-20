package com.wanted.assignment.llm.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wanted.assignment.domain.entity.LlmEntity;
import com.wanted.assignment.domain.repository.LlmRepository;
import com.wanted.assignment.llm.dto.LlmDto;
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
class LlmQueryRepositoryTest {

  private static final String LLM_NAME = "gpt-4o-advanced";
  private static final int LLM_PRICE_PER_TOKEN = 40;

  private static final String UPDATE_LLM_NAME = "gpt-4o-updated";
  private static final int UPDATE_LLM_PRICE_PER_TOKEN = 50;

  @Autowired
  LlmQueryRepository llmQueryRepository;

  @Autowired
  LlmRepository llmRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectLlmList - LLM 목록 조회")
  @Nested
  class TestSelectLlmList {

    @Order(1)
    @DisplayName("조건 없이 전체 조회")
    @Transactional
    @Test
    void testSelectLlmList() {

      // Given & When
      LlmDto.SelectRequest selectRequest = LlmDto.SelectRequest.builder().build();
      List<LlmDto.SelectResponse> llmList = llmQueryRepository.selectLlmList(selectRequest);
      log.debug("llmList: {}", llmList);

      // Then
      assertFalse(llmList.isEmpty());
    }

    @Order(2)
    @DisplayName("이름 조건으로 조회")
    @Transactional
    @Test
    void testSelectLlmListByName() {

      // Given
      String name = "gpt-4o";

      //  When
      LlmDto.SelectRequest selectRequest = LlmDto.SelectRequest.of(name);
      List<LlmDto.SelectResponse> llmList = llmQueryRepository.selectLlmList(selectRequest);
      log.debug("llmList: {}", llmList);

      // Then
      assertFalse(llmList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("updateLlm - LLM 수정")
  @Nested
  class TestUpdateLlm {

    @Order(1)
    @DisplayName("LLM 수정 성공")
    @Transactional
    @Test
    void testUpdateLlmSuccess() {

      // Given
      LlmEntity llmEntity = llmRepository.save(
          LlmEntity.builder()
              .name(LLM_NAME)
              .pricePerToken(LLM_PRICE_PER_TOKEN)
              .isDeleted(false)
              .build()
      );
      clearPersistenceContext();

      // When
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          llmEntity.getId(), UPDATE_LLM_NAME, UPDATE_LLM_PRICE_PER_TOKEN
      );

      boolean isUpdated = llmQueryRepository.updateLlm(updateRequest);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isUpdated),
          () -> {
            LlmEntity updatedEntity = llmRepository.findById(llmEntity.getId()).orElseThrow();
            assertEquals(UPDATE_LLM_NAME, updatedEntity.getName());
            assertEquals(UPDATE_LLM_PRICE_PER_TOKEN, updatedEntity.getPricePerToken());
          }
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteLlmById - LLM 삭제")
  @Nested
  class TestDeleteLlmById {

    @Order(1)
    @DisplayName("LLM 삭제 성공")
    @Transactional
    @Test
    void testDeleteLlmByIdSuccess() {

      // Given
      LlmEntity llmEntity = llmRepository.save(
          LlmEntity.builder()
              .name(LLM_NAME)
              .pricePerToken(LLM_PRICE_PER_TOKEN)
              .build());
      clearPersistenceContext();

      // When
      boolean isDeleted = llmQueryRepository.deleteLlmById(llmEntity.getId());
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isDeleted),
          () -> assertTrue(llmRepository.findById(llmEntity.getId()).get().getIsDeleted())
      );
    }
  }

  @Disabled
  void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }
}
