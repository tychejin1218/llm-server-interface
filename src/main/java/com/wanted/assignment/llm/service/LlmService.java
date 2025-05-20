package com.wanted.assignment.llm.service;

import com.wanted.assignment.common.exception.ApiException;
import com.wanted.assignment.common.type.ApiStatus;
import com.wanted.assignment.domain.entity.LlmEntity;
import com.wanted.assignment.domain.repository.LlmRepository;
import com.wanted.assignment.llm.dto.LlmDto;
import com.wanted.assignment.llm.repository.LlmQueryRepository;
import com.wanted.assignment.llm.repository.LlmUsageQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LlmService {

  private final LlmRepository llmRepository;
  private final LlmQueryRepository llmQueryRepository;
  private final LlmUsageQueryRepository llmUsageQueryRepository;
  private final ModelMapper modelMapper;

  /**
   * LLM 추가
   *
   * @param insertRequest 추가할 LLM 정보
   * @return 추가된 LLM 아이디
   */
  @Transactional
  public Long insertLlm(LlmDto.InsertRequest insertRequest) {

    String name = insertRequest.getName();
    if (llmRepository.existsByName(insertRequest.getName())) {
      log.error("이미 존재하는 LLM 이름으로 LLM 추가 시도: {}", name);
      throw new ApiException(HttpStatus.BAD_REQUEST, ApiStatus.BAD_REQUEST_BODY);
    }

    LlmEntity savedLlm = llmRepository.save(modelMapper.map(insertRequest, LlmEntity.class));
    return savedLlm.getId();
  }

  /**
   * LLM 목록 조회
   *
   * @param selectRequest 조회할 LLM 정보
   * @return LLM 목록
   */
  @Transactional(readOnly = true)
  public List<LlmDto.SelectResponse> getLlmList(LlmDto.SelectRequest selectRequest) {
    return llmQueryRepository.selectLlmList(selectRequest);
  }

  /**
   * LLM 수정
   *
   * @param updateRequest 수정할 LLM 정보
   * @return LLM 삭제 여부
   */
  @Transactional
  public boolean updateLlm(LlmDto.UpdateRequest updateRequest) {
    long llmId = updateRequest.getId();
    if (!llmRepository.existsById(llmId)) {
      log.error("존재하지 않는 LLM 아이디 수정 요청: {}", llmId);
      throw new ApiException(HttpStatus.NOT_FOUND, ApiStatus.LLM_NOT_FOUND);
    }
    return llmQueryRepository.updateLlm(updateRequest);
  }

  /**
   * LLM 삭제
   *
   * @param llmId 삭제할 LLM 아이디
   * @return LLM 삭제 여부
   */
  @Transactional
  public boolean deleteLlmById(Long llmId) {

    if (!llmRepository.existsById(llmId)) {
      log.error("존재하지 않는 LLM 아이디 삭제 요청: {}", llmId);
      throw new ApiException(HttpStatus.NOT_FOUND, ApiStatus.LLM_NOT_FOUND);
    }

    llmUsageQueryRepository.deleteLlmUsageByLlmId(llmId);
    return llmQueryRepository.deleteLlmById(llmId);
  }
}
