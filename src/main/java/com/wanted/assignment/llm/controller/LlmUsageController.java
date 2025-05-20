package com.wanted.assignment.llm.controller;

import com.wanted.assignment.llm.dto.LlmUsageDto;
import com.wanted.assignment.llm.service.LlmUsageService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LlmUsageController implements LlmUsageControllerDocs {

  private final LlmUsageService llmUsageService;

  /**
   * LLM 호출량 기록
   *
   * @param insertRequest 기록할 LLM 사용량 정보
   * @return 201 Created 응답과 생성된 사용자 리소스 URI
   */
  @PostMapping("/usages")
  @Override
  public ResponseEntity<Void> insertLlmUsage(
      @Validated @RequestBody LlmUsageDto.InsertRequest insertRequest) {
    Long llmUsageId = llmUsageService.insertLlmUsage(insertRequest);
    URI location = URI.create("/usages/" + llmUsageId);
    return ResponseEntity.created(location).build();
  }
}
