package com.wanted.assignment.users.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wanted.assignment.common.exception.ApiException;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.domain.repository.UsersRepository;
import com.wanted.assignment.users.dto.UsersDto;
import com.wanted.assignment.users.dto.UsersDto.LlmResponse;
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
class UsersServiceTest {

  private static final long USER_ID = 1L;
  private static final String USER_NAME = "테스터";
  private static final String USER_EMAIL = "tester@wantedlab.com";
  private static final String USER_PASSWORD = "Password1!";

  @Autowired
  UsersService usersService;

  @Autowired
  UsersRepository usersRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("insertUser - 사용자 추가")
  @Nested
  class TestInsertUser {

    @Order(1)
    @DisplayName("사용자 추가 성공")
    @Transactional
    @Test
    void testInsertUserSuccess() {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );

      // When
      Long userId = usersService.insertUser(insertRequest);

      // Then
      assertAll(
          () -> assertNotNull(userId)
      );
    }

    @Order(2)
    @DisplayName("중복된 이메일로 사용자 추가 실패")
    @Transactional
    @Test
    void testInsertUserDuplicateEmail() {

      // Given
      UsersDto.InsertRequest existsRequest = UsersDto.InsertRequest.of(
          "테스터01", USER_EMAIL, USER_PASSWORD
      );
      usersService.insertUser(existsRequest);
      clearPersistenceContext();

      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          "테스터02", USER_EMAIL, USER_PASSWORD
      );

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class, () -> usersService.insertUser(insertRequest));

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
  @DisplayName("getUserList - 사용자 목록 조회")
  @Nested
  class TestGetUserList {

    @Order(1)
    @DisplayName("사용자 목록 조회 성공")
    @Transactional
    @Test
    void testGetUserListSuccess() {

      // Given
      UsersDto.SelectRequest selectRequest = UsersDto.SelectRequest.builder().build();

      // When
      List<UsersDto.SelectResponse> userList = usersService.getUserList(selectRequest);

      // Then
      assertFalse(userList.isEmpty());
    }

    @Order(2)
    @DisplayName("사용자 목록이 없는 경우 조회 성공")
    @Transactional
    @Test
    void testGetUserListEmpty() {

      // Given
      UsersDto.SelectRequest selectRequest = UsersDto.SelectRequest.builder().build();
      usersRepository.deleteAll();

      // When
      List<UsersDto.SelectResponse> userList = usersService.getUserList(selectRequest);

      // Then
      assertTrue(userList.isEmpty());
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("deleteUserById - 사용자 삭제")
  @Nested
  class TestDeleteUserById {

    @Order(1)
    @DisplayName("사용자 삭제 성공")
    @Transactional
    @Test
    void testDeleteUserSuccess() {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );
      Long userId = usersService.insertUser(insertRequest);

      // When
      boolean isDeleted = usersService.deleteUserById(userId);
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isDeleted),
          () -> assertTrue(usersRepository.findById(userId).get().getIsDeleted())
      );
    }

    @Order(2)
    @DisplayName("존재하지 않는 사용자 아이디로 삭제 실패")
    @Transactional
    @Test
    void testDeleteUserByIdNonExistingUserId() {

      // Given
      long nonExistingUserId = 0L;

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class,
          () -> usersService.deleteUserById(nonExistingUserId)
      );

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.USER_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.USER_NOT_FOUND.getMessage(),
              apiException.getStatus().getMessage())
      );
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
    void testGetUserLlmResponseSuccess() {

      //  Given & When
      LlmResponse llmResponse = usersService.getUserLlmResponse(USER_ID);

      // Then
      assertAll(
          () -> assertNotNull(llmResponse),
          () -> assertNotNull(llmResponse.getUserUsages()),
          () -> assertNotNull(llmResponse.getLlmUsages())
      );
    }

    @Order(2)
    @DisplayName("특정 사용자의 LLM 사용량이 존재하지 않는 경우")
    @Transactional
    @Test
    void testGetUserLlmResponseNoUsages() {

      // Given
      UsersDto.InsertRequest insertRequest = UsersDto.InsertRequest.of(
          USER_NAME, USER_EMAIL, USER_PASSWORD
      );
      Long userId = usersService.insertUser(insertRequest);

      // When
      LlmResponse llmResponse = usersService.getUserLlmResponse(userId);

      // Then
      assertAll(
          () -> assertNotNull(llmResponse),
          () -> assertEquals(0, llmResponse.getUserUsages().getTotalUsedToken()),
          () -> assertEquals(0, llmResponse.getUserUsages().getTotalPrice()),
          () -> assertTrue(llmResponse.getLlmUsages().isEmpty())
      );
    }

    @Order(3)
    @DisplayName("존재하지 않는 사용자 아이디로 조회 실패")
    @Transactional
    @Test
    void testGetUserLlmResponseNonExistingUserId() {

      // Given
      long nonExistingUserId = 0L;

      // When
      ApiException apiException = Assertions.assertThrows(
          ApiException.class,
          () -> usersService.getUserLlmResponse(nonExistingUserId)
      );

      // Then
      assertAll(
          () -> assertEquals(ApiStatus.USER_NOT_FOUND.getCode(),
              apiException.getStatus().getCode()),
          () -> assertEquals(ApiStatus.USER_NOT_FOUND.getMessage(),
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
