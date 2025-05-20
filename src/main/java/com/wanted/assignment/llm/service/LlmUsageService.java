package com.wanted.assignment.llm.service;

import com.wanted.assignment.domain.entity.LlmUsageEntity;
import com.wanted.assignment.domain.repository.LlmUsageRepository;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import com.wanted.assignment.llm.repository.LlmUsageQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LlmUsageService {

  private final LlmUsageRepository llmUsageRepository;
  private final LlmUsageQueryRepository llmUsageQueryRepository;
  private final ModelMapper modelMapper;

  /**
   * LLM 호출량 기록
   *
   * @param insertRequest 기록할 LLM 사용량 정보
   * @return 기록된 LLM 사용량 아이디
   */
  @Transactional
  public Long insertLlmUsage(LlmUsageDto.InsertRequest insertRequest) {
    LlmUsageEntity savedLlmUsage = llmUsageRepository.save(
        modelMapper.map(insertRequest, LlmUsageEntity.class));
    return savedLlmUsage.getId();
  }

  /**
   * LLM별 사용량 조회
   *
   * @return LLM별 사용량 정보
   */
  @Transactional(readOnly = true)
  public List<LlmUsageDto.StatsResponse> getLlmUsageStats() {
    return llmUsageQueryRepository.selectLlmUsageStats();
  }
}
