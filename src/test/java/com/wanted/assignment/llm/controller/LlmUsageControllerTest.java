package com.wanted.assignment.llm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import com.wanted.assignment.llm.service.LlmUsageService;
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
class LlmUsageControllerTest {

  private static final String LLM_USAGE_BASE_URL = "/usages";

  private static final long USER_ID = 1L;
  private static final long LLM_ID = 1L;
  private static final int USED_TOKEN = 512;

  private static final String PATH_CODE = "$.code";
  private static final String PATH_MESSAGE = "$.message";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  LlmUsageService llmUsageService;

  @Autowired
  ObjectMapper objectMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertLlmUsage - LLM 사용량 기록 API")
  @Nested
  class TestInsertLlmUsage {

    @Order(1)
    @DisplayName("LLM 사용량 기록 성공")
    @Transactional
    @Test
    void testInsertLlmUsageSuccess() throws Exception {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, LLM_ID, USED_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_USAGE_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

      // Then
      resultActions
          .andExpect(status().isCreated())
          .andExpect(header().exists(HttpHeaders.LOCATION))
          .andDo(print());
    }

    @Order(2)
    @DisplayName("사용자 아이디가 null인 경우 LLM 사용량 기록 실패")
    @Transactional
    @Test
    void testInsertLlmUsageFailNullUserId() throws Exception {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          null, LLM_ID, USED_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_USAGE_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
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
    @DisplayName("LLM 아이디가 null인 경우 LLM 사용량 기록 실패")
    @Transactional
    @Test
    void testInsertLlmUsageFailNullLlmId() throws Exception {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, null, USED_TOKEN
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_USAGE_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
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
    @DisplayName("LLM 사용량이 음수일 경우 LLM 사용량 기록 실패")
    @Transactional
    @Test
    void testInsertLlmUsageFailNegativeUsedToken() throws Exception {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, LLM_ID, -512
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_USAGE_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
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

    @Order(5)
    @DisplayName("LLM 사용량이 0일 경우 LLM 사용량 기록 실패")
    @Transactional
    @Test
    void testInsertLlmUsageFailZeroUsedToken() throws Exception {

      // Given
      LlmUsageDto.InsertRequest insertRequest = LlmUsageDto.InsertRequest.of(
          USER_ID, LLM_ID, 0
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(LLM_USAGE_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
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
  }
}
