package com.wanted.assignment.users.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.users.dto.UsersDto;
import com.wanted.assignment.users.service.UsersService;
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
class UsersControllerTest {

  private static final String USERS_BASE_URL = "/users";
  private static final String USERS_USAGES_PATH = "/usages";

  private static final long USER_ID = 1L;
  private static final String USER_NAME = "테스터";
  private static final String USER_EMAIL = "tester@wantedlab.com";
  private static final String USER_PASSWORD = "Password1!";

  private static final String PATH_CODE = "$.code";
  private static final String PATH_MESSAGE = "$.message";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UsersService usersService;

  @Autowired
  ObjectMapper objectMapper;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertUsers - 사용자 추가 API")
  @Nested
  class TestInsertTodo {

    @Order(1)
    @DisplayName("사용자 추가 성공")
    @Transactional
    @Test
    void testInsertUserSuccess() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

      // Then
      resultActions
          .andExpect(status().isCreated())
          .andExpect(header().exists(HttpHeaders.LOCATION))
          .andDo(print());
    }

    @Order(2)
    @DisplayName("이름이 공백 또는 빈 값인 경우 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailBlankName() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          "", USER_EMAIL, USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

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
    @DisplayName("이름이 최대 길이(16)를 초과한 경우 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailMaxLengthName() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME.repeat(6), USER_EMAIL, USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest)));

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(4)
    @DisplayName("이름에 한글/영어/숫자가 아닌 특수 문자(!)가 포함되어 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailInvalidName() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          "Invalid@Name!", USER_EMAIL, USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(
              ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(5)
    @DisplayName("이메일이 빈 값인 경우 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailBlankEmail() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, "", USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
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

    @Order(6)
    @DisplayName("이메일 형식이 잘못된 경우 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailInvalidEmail() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, "invalid-email", USER_PASSWORD
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
      );

      // Then
      resultActions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath(PATH_CODE).value(
              ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(
              ApiStatus.METHOD_ARGUMENT_NOT_VALID.getMessage()))
          .andDo(print());
    }

    @Order(7)
    @DisplayName("비밀번호가 최소 길이(8자 미만)일 때 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserFailMinLengthPassword() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD.substring(0, 7)
      );

      // When
      ResultActions resultActions = mockMvc.perform(
          post(USERS_BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insertRequest))
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
  @DisplayName("getUserList - 사용자 목록 조회 API")
  @Nested
  class TestGetUserList {

    @Order(1)
    @DisplayName("사용자 목록 조회 성공")
    @Transactional
    @Test
    void testGetTodoListSuccess() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.builder().build();

      // When
      ResultActions resultActions = mockMvc.perform(
          get(USERS_BASE_URL)
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
  @DisplayName("deleteUserById - 사용자 삭제 API")
  @Nested
  class TestDeleteUserById {

    @Order(1)
    @DisplayName("사용자 삭제 성공")
    @Transactional
    @Test
    void testDeleteUserByIdSuccess() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );
      Long userId = usersService.insertUser(insertRequest);

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(USERS_BASE_URL + "/" + userId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    @Order(2)
    @DisplayName("존재하지 않는 사용자 아이디로 삭제 실패")
    @Transactional
    @Test
    void testDeleteUserByIdNonExistingUserId() throws Exception {

      // Given
      long nonExistingUserId = 9999L;

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(USERS_BASE_URL + "/" + nonExistingUserId)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // Then
      resultActions
          .andExpect(status().isNotFound())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.USER_NOT_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.USER_NOT_FOUND.getMessage()))
          .andDo(print());
    }

    @Order(3)
    @DisplayName("userId가 빈 값인 경우 삭제 실패")
    @Test
    void testDeleteUserByNullUserId() throws Exception {

      // Given
      String invalidUserId = "";

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(USERS_BASE_URL + "/" + invalidUserId)
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
    @DisplayName("userId가 문자열인 경우 삭제 실패")
    @Test
    void testDeleteUserByStrUserId() throws Exception {

      // Given
      String strUserId = "사용자아이디";

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(USERS_BASE_URL + "/" + strUserId)
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
    @DisplayName("userId가 음수인 경우 삭제 실패")
    @Test
    void testDeleteUserByNegativeUserId() throws Exception {

      // Given
      long userId = -1L;

      // When
      ResultActions resultActions = mockMvc.perform(
          delete(USERS_BASE_URL + "/" + userId)
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
  @DisplayName("getUserLlmResponse - 특정 사용자 LLM 사용량 조회")
  @Nested
  class TestGetUserLlmResponse {

    @Order(1)
    @DisplayName("특정 사용자 LLM 사용량 조회 성공")
    @Transactional
    @Test
    void testGetUserLlmResponseSuccess() throws Exception {

      // Given & When
      ResultActions resultActions = mockMvc.perform(
          get(USERS_BASE_URL + "/" + USER_ID + USERS_USAGES_PATH)
              .contentType(MediaType.APPLICATION_JSON));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andDo(print());
    }

    @Order(2)
    @DisplayName("특정 사용자의 LLM 사용량이 존재하지 않는 경우")
    @Transactional
    @Test
    void testGetUserLlmResponseNoUsages() throws Exception {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );
      long userId = usersService.insertUser(insertRequest);

      // When
      ResultActions resultActions = mockMvc.perform(
          get(USERS_BASE_URL + "/" + userId + USERS_USAGES_PATH)
              .contentType(MediaType.APPLICATION_JSON));

      // Then
      resultActions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.userUsages.totalUsedToken").value(0))
          .andExpect(jsonPath("$.userUsages.totalPrice").value(0))
          .andExpect(jsonPath("$.llmUsages").isEmpty())
          .andDo(print());
    }

    @Order(3)
    @DisplayName("존재하지 않는 사용자 아이디로 조회 실패")
    @Transactional
    @Test
    void testGetUserLlmResponseExistingUserId() throws Exception {

      // Given
      long nonExistingUserId = 9999L;

      // When
      ResultActions resultActions = mockMvc.perform(
          get(USERS_BASE_URL + "/" + nonExistingUserId + USERS_USAGES_PATH)
              .contentType(MediaType.APPLICATION_JSON));

      // Then
      resultActions
          .andExpect(status().isNotFound())
          .andExpect(jsonPath(PATH_CODE).value(ApiStatus.USER_NOT_FOUND.getCode()))
          .andExpect(jsonPath(PATH_MESSAGE).value(ApiStatus.USER_NOT_FOUND.getMessage()))
          .andDo(print());
    }
  }
}
