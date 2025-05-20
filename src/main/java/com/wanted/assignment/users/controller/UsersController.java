package com.wanted.assignment.users.controller;

import com.wanted.assignment.users.dto.UsersDto;
import com.wanted.assignment.users.dto.UsersDto.LlmResponse;
import com.wanted.assignment.users.dto.UsersDto.SelectResponse;
import com.wanted.assignment.users.service.UsersService;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UsersController implements UserControllerDocs {

  private final UsersService usersService;

  /**
   * 사용자 추가
   *
   * @param insertRequest 추가할 사용자 정보
   * @return 201 Created 응답과 생성된 사용자 리소스 URI
   */
  @PostMapping("/users")
  @Override
  public ResponseEntity<Void> insertUser(
      @Validated @RequestBody UsersDto.InsertRequest insertRequest) {
    Long userId = usersService.insertUser(insertRequest);
    URI location = URI.create("/users/" + userId);
    return ResponseEntity.created(location).build();
  }

  /**
   * 사용자 목록 조회
   *
   * @param name  설명
   * @param email 설명
   * @return 200 OK 응답과 사용자 목록
   */
  @GetMapping("/users")
  @Override
  public ResponseEntity<List<SelectResponse>> getUserList(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "email", required = false) String email) {
    List<SelectResponse> userList = usersService.getUserList(
        UsersDto.SelectRequest.of(name, email)
    );
    return ResponseEntity.ok(userList);
  }

  /**
   * 사용자 삭제
   *
   * @param userId 사용자 아이디
   * @return 204 No Content 응답
   */
  @DeleteMapping("/users/{user_id}")
  @Override
  public ResponseEntity<Void> deleteUserById(@PathVariable("user_id") @Positive Long userId) {
    usersService.deleteUserById(userId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 특정 사용자 LLM 사용량 조회
   *
   * @param userId 사용자 아이디
   * @return 특정 사용자 LLM 사용량 정보
   */
  @GetMapping("/users/{user_id}/usages")
  @Override
  public ResponseEntity<LlmResponse> getUserLlmResponse(
      @PathVariable("user_id") @Positive Long userId) {
    return ResponseEntity.ok(usersService.getUserLlmResponse(userId));
  }
}
