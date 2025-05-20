package com.wanted.assignment.users.service;

import com.wanted.assignment.common.exception.ApiException;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.domain.entity.UsersEntity;
import com.wanted.assignment.domain.repository.UsersRepository;
import com.wanted.assignment.users.dto.UsersDto;
import com.wanted.assignment.users.dto.UsersDto.LlmResponse;
import com.wanted.assignment.users.repository.UsersQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

  private final UsersRepository usersRepository;
  private final UsersQueryRepository usersQueryRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  /**
   * 사용자 추가
   *
   * @param insertRequest 추가할 사용자 정보
   * @return 추가된 사용자 아이디
   */
  @Transactional
  public Long insertUser(UsersDto.InsertRequest insertRequest) {

    String email = insertRequest.getEmail();
    if (usersRepository.existsByEmail(email)) {
      log.error("이미 존재하는 이메일로 사용자 추가 시도: {}", email);
      throw new ApiException(HttpStatus.BAD_REQUEST, ApiStatus.BAD_REQUEST_BODY);
    }

    UsersEntity userEntity = modelMapper.map(insertRequest, UsersEntity.class);
    userEntity.setPassword(passwordEncoder.encode(insertRequest.getPassword()));

    UsersEntity savedUser = usersRepository.save(userEntity);
    return savedUser.getId();
  }

  /**
   * 사용자 목록 조회
   *
   * @param selectRequest 조회할 사용자 정보
   * @return 사용자 목록
   */
  @Transactional(readOnly = true)
  public List<UsersDto.SelectResponse> getUserList(UsersDto.SelectRequest selectRequest) {
    return usersQueryRepository.selectUserList(selectRequest);
  }

  /**
   * 사용자 삭제
   *
   * @param userId 삭제할 사용자 아이디
   * @return 사용자 삭제 여부
   */
  @Transactional
  public boolean deleteUserById(Long userId) {
    if (!usersRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 아이디 삭제 요청: {}", userId);
      throw new ApiException(HttpStatus.NOT_FOUND, ApiStatus.USER_NOT_FOUND);
    }
    return usersQueryRepository.deleteUserById(userId);
  }

  /**
   * 특정 사용자 LLM 사용량 조회
   *
   * @param userId 삭제할 사용자 아이디
   * @return 특정 사용자 LLM 사용량 정보
   */
  public LlmResponse getUserLlmResponse(Long userId) {

    if (!usersRepository.existsById(userId)) {
      log.error("존재하지 않는 사용자 아이디 조회 요청: {}", userId);
      throw new ApiException(HttpStatus.NOT_FOUND, ApiStatus.USER_NOT_FOUND);
    }

    List<UsersDto.LlmUsage> llmUsages = usersQueryRepository.selectUserLlmUsage(userId);

    int totalPrice = 0;
    int totalUsedToken = 0;
    for (UsersDto.LlmUsage usage : llmUsages) {
      totalPrice += usage.getTotalPrice();
      totalUsedToken += usage.getTotalUsedToken();
    }

    UsersDto.UserUsage userUsage = UsersDto.UserUsage.of(totalUsedToken, totalPrice);

    return LlmResponse.of(userUsage, llmUsages);
  }
}
