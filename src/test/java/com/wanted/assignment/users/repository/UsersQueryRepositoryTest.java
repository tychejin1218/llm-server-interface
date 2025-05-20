package com.wanted.assignment.users.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wanted.assignment.domain.entity.UsersEntity;
import com.wanted.assignment.domain.repository.UsersRepository;
import com.wanted.assignment.users.dto.UsersDto;
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
class UsersQueryRepositoryTest {

  private static final long USER_ID = 1L;
  private static final String USER_NAME = "테스터";
  private static final String USER_EMAIL = "tester@wantedlab.com";
  private static final String USER_PASSWORD = "Password1!";

  @Autowired
  UsersQueryRepository usersQueryRepository;

  @Autowired
  UsersRepository usersRepository;

  @Autowired
  EntityManager entityManager;

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectUserList - 사용자 목록 조회")
  @Nested
  class TestSelectUserList {

    @Order(1)
    @DisplayName("조건 없이 전체 조회 성공")
    @Transactional
    @Test
    void testSelectUserListSuccess() {

      // Given & When
      UsersDto.SelectRequest selectRequest = UsersDto.SelectRequest.builder().build();
      List<UsersDto.SelectResponse> userList = usersQueryRepository.selectUserList(selectRequest);
      log.debug("userList: {}", userList);

      // Then
      assertFalse(userList.isEmpty());
    }

    @Order(2)
    @DisplayName("이름 조건으로 조회")
    @Transactional
    @Test
    void testSelectUserListByName() {

      // Given
      String name = "김티드";

      //  When
      UsersDto.SelectRequest selectRequest = UsersDto.SelectRequest.of(name, null);
      List<UsersDto.SelectResponse> userList = usersQueryRepository.selectUserList(selectRequest);
      log.debug("userList: {}", userList);

      // Then
      assertFalse(userList.isEmpty());
    }

    @Order(3)
    @DisplayName("이메일 조건으로 조회")
    @Transactional
    @Test
    void testSelectUserListByEmail() {

      // Given
      String email = "kimted@wantedlab.com";

      //  When
      UsersDto.SelectRequest selectRequest = UsersDto.SelectRequest.of(null, email);
      List<UsersDto.SelectResponse> userList = usersQueryRepository.selectUserList(selectRequest);
      log.debug("userList: {}", userList);

      // Then
      assertFalse(userList.isEmpty());
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
    void testDeleteUserByIdSuccess() {

      // Given
      UsersEntity usersEntity = usersRepository.save(
          UsersEntity.builder()
              .name(USER_NAME)
              .email(USER_EMAIL)
              .password(USER_PASSWORD)
              .build());
      clearPersistenceContext();

      // When
      boolean isDeleted = usersQueryRepository.deleteUserById(usersEntity.getId());
      clearPersistenceContext();

      // Then
      assertAll(
          () -> assertTrue(isDeleted),
          () -> assertTrue(usersRepository.findById(usersEntity.getId()).get().getIsDeleted())
      );
    }
  }

  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("selectUserLlmList - 특정 사용자별 LLM 사용량 조회")
  @Nested
  class TestSelectUserLlmList {

    @Order(1)
    @DisplayName("특정 사용자별 LLM 사용량 조회 성공")
    @Transactional
    @Test
    void testelectUserLlmUsageSuccess() {

      // Given & When
      List<UsersDto.LlmUsage> selectUserLlmUsage =
          usersQueryRepository.selectUserLlmUsage(USER_ID);

      // Then
      assertFalse(selectUserLlmUsage.isEmpty());
    }
  }

  @Disabled
  void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }
}
