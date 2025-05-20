package com.wanted.assignment.llm.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wanted.assignment.common.exception.ApiException;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.domain.entity.LlmEntity;
import com.wanted.assignment.domain.repository.LlmRepository;
import com.wanted.assignment.domain.repository.LlmUsageRepository;
import com.wanted.assignment.llm.dto.LlmDto;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
class LlmServiceTest {

  private static final String LLM_NAME = "gpt-4o-advanced";
  private static final int LLM_PRICE_PER_TOKEN = 40;

  private static final String UPDATE_LLM_NAME = "gpt-4o-updated";
  private static final int UPDATE_LLM_PRICE_PER_TOKEN = 50;

  private static final Long USER_ID = 1L;
  private static final int USED_TOKEN = 512;

  @Autowired
  LlmService llmService;

  @Autowired
  LlmUsageService llmUsageService;

  @Autowired
  LlmRepository llmRepository;

  @Autowired
  LlmUsageRepository llmUsageRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertLlm - LLM 추가")
  @Nested
  class TestInsertLlm {

    @Order(1)
    @DisplayName("LLM 추가 성공")
    @Transactional
    @Test
    void testInsertLlmSuccess() {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );

      // When
      long llmId = llmService.insertLlm(insertRequest);

      // Then
      assertAll(
          () -> assertNotNull(llmId)
      );
    }

    @Order(2)
    @DisplayName("중복된 이름으로 LLM 추가 실패")
    @Transactional
    @Test
    void testInsertLlmDuplicateEmail() {

      // Given
      LlmDto.InsertRequest existsRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );
      llmService.insertLlm(existsRequest);
      clearPersistenceContext();

      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> llmService.insertLlm(insertRequest));

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.BAD_REQUEST_BODY.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.BAD_REQUEST_BODY.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getLlmList - LLM 목록 조회")
  @Nested
  class TestGetLlmList {

    @Order(1)
    @DisplayName("LLM 목록 조회 성공")
    @Transactional
    @Test
    void testGetLlmListSuccess() {

      // Given
      LlmDto.SelectRequest selectRequest = LlmDto.SelectRequest.builder().build();

      // When
      List<LlmDto.SelectResponse> llmList = llmService.getLlmList(selectRequest);

      // Then
      assertFalse(llmList.isEmpty());
    }

    @Order(2)
    @DisplayName("LLM 목록이 없는 경우 조회 성공")
    @Transactional
    @Test
    void testGetLlmListEmpty() {

      // Given
      LlmDto.SelectRequest selectRequest = LlmDto.SelectRequest.builder().build();
      llmRepository.deleteAll();

      // When
      List<LlmDto.SelectResponse> llmList = llmService.getLlmList(selectRequest);

      // Then
      assertTrue(llmList.isEmpty());
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
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );
      Long llmId = llmService.insertLlm(insertRequest);
      clearPersistenceContext();

      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          llmId, UPDATE_LLM_NAME, UPDATE_LLM_PRICE_PER_TOKEN
      );

      // When
      boolean isUpdated = llmService.updateLlm(updateRequest);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isUpdated),
          () -> {
            LlmEntity updatedEntity = llmRepository.findById(llmId).orElseThrow();
            assertEquals(UPDATE_LLM_NAME, updatedEntity.getName());
            assertEquals(UPDATE_LLM_PRICE_PER_TOKEN, updatedEntity.getPricePerToken());
          }
      );
    }

    @Order(2)
    @DisplayName("존재하지 않은 LLM 아이디로 수정 실패")
    @Transactional
    @Test
    void testUpdateLlmFailNonExistingLlmId() {

      // Given
      long nonExistingLlmId = 0L;
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          nonExistingLlmId, UPDATE_LLM_NAME, UPDATE_LLM_PRICE_PER_TOKEN
      );

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class,
          () -> llmService.updateLlm(updateRequest)
      );

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.LLM_NOT_FOUND.getCode(), apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.LLM_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
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
    void testDeleteLlmSuccess() {

      // Given
      LlmDto.InsertRequest insertLlmRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );
      long llmId = llmService.insertLlm(insertLlmRequest);

      LlmUsageDto.InsertRequest insertLlmUsageRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, llmId, USED_TOKEN
      );
      long llmUsageId = llmUsageService.insertLlmUsage(insertLlmUsageRequest);

      // When
      boolean isDeleted = llmService.deleteLlmById(llmId);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isDeleted),
          () -> assertTrue(llmRepository.findById(llmId).get().getIsDeleted()),
          () -> assertTrue(llmUsageRepository.findById(llmUsageId).get().getIsDeleted())
      );
    }

    @Order(2)
    @DisplayName("존재하지 않는 LLM 아이디로 삭제 실패")
    @Transactional
    @Test
    void testDeleteLlmByIdNonExistingLlmId() {

      // Given
      long nonExistingLlmId = 0L;

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class,
          () -> llmService.deleteLlmById(nonExistingLlmId)
      );

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.LLM_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.LLM_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
      );
    }
  }

  @Disabled
  void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }
}
