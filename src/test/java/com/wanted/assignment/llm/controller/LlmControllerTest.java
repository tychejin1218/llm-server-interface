package com.wanted.assignment.llm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.llm.dto.LlmDto;
import com.wanted.assignment.llm.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("local")
@SpringBootTest
class LlmControllerTest {

  private static final String LLM_BASE_URL = "/llm";
  private static final String LLM_UPDATE_URL = "/llm/{llmId}";
  private static final String LLM_USAGES_URL = "/llm/usages";

  private static final String LLM_NAME = "gpt-4o-advanced";
  private static final int LLM_PRICE_PER_TOKEN = 40;

  private static final String UPDATE_LLM_NAME = "gpt-4o-updated";
  private static final int UPDATE_LLM_PRICE_PER_TOKEN = 50;

  private static final String PATH_CODE = "$.code";
  private static final String PATH_MESSAGE = "$.message";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  LlmService llmService;

  @Autowired
  ObjectMapper objectMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertLlm - LLM 추가 API")
  @Nested
  class TestInsertLlm {

    @Order(1)
    @DisplayName("LLM 추가 성공")
    @Transactional
    @Test
    void testInsertLlmSuccess() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

      // Then
      resultActions
          .andExpect(status().isCreated())
          .andExpect(header().exists(HttpHeaders.LOCATION))
          .andDo(print());
    }

    @Order(2)
    @DisplayName("이름이 공백 또는 빈 값인 경우 LLM 추가 실패")
    @Transactional
    @Test
    void testInsertLlmFailBlankName() throws Exception {

      // Given
      LlmDto.InsertRequest invalidRequest = LlmDto.InsertRequest.of(
          "", LLM_PRICE_PER_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(
              ApiStatus.MISSING_SERVLET_REQUEST_PARAMETER.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(
              ApiStatus.MISSING_SERVLET_REQUEST_PARAMETER.getMessage()))
          .andDo(print());
    }

    @Order(3)
    @DisplayName("이름이 최대 길이(20)를 초과한 경우 LLM 추가 실패")
    @Transactional
    @Test
    void testInsertLlmFailMaxLengthName() throws Exception {

      // Given
      LlmDto.InsertRequest invalidRequest = LlmDto.InsertRequest.of(
          LLM_NAME.repeat(3), LLM_PRICE_PER_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(4)
    @DisplayName("LLM 토큰당 가격이 음수일 경우 LLM 추가 실패")
    @Transactional
    @Test
    void testInsertLlmFailNegativePricePerToken() throws Exception {

      // Given
      LlmDto.InsertRequest invalidRequest = LlmDto.InsertRequest.of(
          LLM_NAME, -10
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(5)
    @DisplayName("LLM 토큰당 가격이 0일 경우 LLM 추가 실패")
    @Transactional
    @Test
    void testInsertLlmFailZeroPricePerToken() throws Exception {

      // Given
      LlmDto.InsertRequest invalidRequest = LlmDto.InsertRequest.of(
          LLM_NAME, 0
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getLlmList - LLM 목록 조회 API")
  @Nested
  class TestGetLlmList {

    @Order(1)
    @DisplayName("LLM 목록 조회 성공")
    @Transactional
    @Test
    void testGetLlmListSuccess() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.builder().build();

      // When
      ResultActions resultActions = mockMvc.perform(
          get(LLM_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("updateLlm - LLM 수정 API")
  @Nested
  class TestUpdateLlm {

    @Order(1)
    @DisplayName("LLM 수정 성공")
    @Transactional
    @Test
    void testUpdateLlmSuccess() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(LLM_NAME, LLM_PRICE_PER_TOKEN);
      Long llmId = llmService.insertLlm(insertRequest);

      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          UPDATE_LLM_NAME, UPDATE_LLM_PRICE_PER_TOKEN);

      // When
      ResultActions resultActions = mockMvc.perform(
          put(LLM_UPDATE_URL, llmId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest))
      );

      // Then
      resultActions
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    @Order(2)
    @DisplayName("존재하지 않은 LLM 아이디로 수정 실패")
    @Transactional
    @Test
    void testUpdateLlmFailNonExistingLlmId() throws Exception {

      // Given
      long nonExistingLlmId = 9999L;
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          UPDATE_LLM_NAME, UPDATE_LLM_PRICE_PER_TOKEN);

      // When
      ResultActions resultActions = mockMvc.perform(
          put(LLM_UPDATE_URL, nonExistingLlmId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest))
      );

      // Then
      resultActions
          .andExpect(status().isNotFound())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.LLM_NOT_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.LLM_NOT_FOUND.getMessage()))
          .andDo(print());
    }

    @Order(3)
    @DisplayName("이름이 공백 또는 인 경우 LLM 수정 실패")
    @Transactional
    @Test
    void testUpdateLlmFailBlankName() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(LLM_NAME, LLM_PRICE_PER_TOKEN);
      Long llmId = llmService.insertLlm(insertRequest);
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          llmId, "", LLM_PRICE_PER_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          put(LLM_UPDATE_URL, llmId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(
              ApiStatus.MISSING_SERVLET_REQUEST_PARAMETER.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(
              ApiStatus.MISSING_SERVLET_REQUEST_PARAMETER.getMessage()))
          .andDo(print());
    }

    @Order(4)
    @DisplayName("LLM 토큰당 가격이 음수일 경우 LLM 수정 실패")
    @Transactional
    @Test
    void testUpdateLlmFailNegativePricePerToken() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(LLM_NAME, LLM_PRICE_PER_TOKEN);
      Long llmId = llmService.insertLlm(insertRequest);
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          llmId, LLM_NAME, -10
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          put(LLM_UPDATE_URL, llmId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(5)
    @DisplayName("LLM 토큰당 가격이 0일 경우 LLM 수정 실패")
    @Transactional
    @Test
    void testUpdateLlmFailZeroPricePerToken() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(LLM_NAME, LLM_PRICE_PER_TOKEN);
      Long llmId = llmService.insertLlm(insertRequest);
      LlmDto.UpdateRequest updateRequest = LlmDto.UpdateRequest.of(
          llmId, LLM_NAME, 0
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          put(LLM_UPDATE_URL, llmId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteLlmById - LLM 삭제 API")
  @Nested
  class TestDeleteLlmById {

    @Order(1)
    @DisplayName("LLM 삭제 성공")
    @Transactional
    @Test
    void testDeleteLlmByIdSuccess() throws Exception {

      // Given
      LlmDto.InsertRequest insertRequest = LlmDto.InsertRequest.of(
          LLM_NAME, LLM_PRICE_PER_TOKEN
      );
      Long llmId = llmService.insertLlm(insertRequest);

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(LLM_BASE_URL + "/" + llmId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    @Order(2)
    @DisplayName("존재하지 않는 LLM 아이디로 삭제 실패")
    @Transactional
    @Test
    void testDeleteLlmByIdNonExistingLlmId() throws Exception {

      // Given
      long nonExistingLlmId = 9999L;

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(LLM_BASE_URL + "/" + nonExistingLlmId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isNotFound())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.LLM_NOT_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.LLM_NOT_FOUND.getMessage()))
          .andDo(print());
    }

    @Order(3)
    @DisplayName("llmId가 빈 값인 경우 삭제 실패")
    @Test
    void testDeleteLlmByNullLlmId() throws Exception {

      // Given
      String invalidLlmId = "";

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(LLM_BASE_URL + "/" + invalidLlmId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isNotFound())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.NO_RESOURCE_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.NO_RESOURCE_FOUND.getMessage()))
          .andDo(print());
    }

    @Order(4)
    @DisplayName("llmId가 문자열인 경우 삭제 실패")
    @Test
    void testDeleteLlmByStrLlmId() throws Exception {

      // Given
      String strLlmId = "LLM아이디";

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(LLM_BASE_URL + "/" + strLlmId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(
              ApiStatus.METHOD_ARGUMENT_TYPE_MISMATCH.getCode()))
          .andExpect(
              jsonPath(PATH_MESSAGE).value(
                  ApiStatus.METHOD_ARGUMENT_TYPE_MISMATCH.getMessage()))
          .andDo(print());
    }

    @Order(5)
    @DisplayName("llmId가 음수인 경우 삭제 실패")
    @Test
    void testDeleteLlmByNegativeLlmId() throws Exception {

      // Given
      long llmId = -1L;

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(LLM_BASE_URL + "/" + llmId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("getLlmUsageStats - LLM별 사용량 조회 API")
  @Nested
  class TestGetLlmUsageStats {

    @Order(1)
    @DisplayName("LLM별 사용량 조회 성공")
    @Transactional
    @Test
    void testGetLlmUsageStatsSuccess() throws Exception {

      // Given & When
      ResultActions resultActions = mockMvc.perform(
          get(LLM_USAGES_URL)
              .contentType(MediaType.APPLICATION_JSON));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andDo(print());
    }
  }
}
