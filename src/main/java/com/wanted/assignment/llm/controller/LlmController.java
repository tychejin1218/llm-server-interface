package com.wanted.assignment.llm.controller;

import com.wanted.assignment.llm.dto.LlmDto;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import com.wanted.assignment.llm.service.LlmService;
import com.wanted.assignment.llm.service.LlmUsageService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LlmController implements LlmControllerDocs {

  private final LlmService llmService;
  private final LlmUsageService llmUsageService;

  /**
   * LLM 추가
   *
   * @param insertRequest 추가할 LLM 정보
   * @return 201 Created 응답과 생성된 사용자 리소스 URI
   */
  @PostMapping("/llm")
  @Override
  public ResponseEntity<Void> insertLlm(
      @Validated @RequestBody LlmDto.InsertRequest insertRequest) {
    Long llmId = llmService.insertLlm(insertRequest);
    URI location = URI.create("/llm/" + llmId);
    return ResponseEntity.created(location).build();
  }

  /**
   * LLM 목록 조회
   *
   * @param name 설명
   * @return 200 OK 응답과 사용자 목록
   */
  @GetMapping("/llm")
  @Override
  public ResponseEntity<List<LlmDto.SelectResponse>> getLlmList(
      @RequestParam(value = "name", required = false) String name) {
    List<LlmDto.SelectResponse> llmList = llmService.getLlmList(
        LlmDto.SelectRequest.of(name)
    );
    return ResponseEntity.ok(llmList);
  }

  /**
   * LLM 수정
   *
   * @param llmId         수정할 LLM의 ID
   * @param updateRequest 수정할 LLM 정보
   * @return 204 No Content 응답
   */
  @PutMapping("/llm/{llm_id}")
  @Override
  public ResponseEntity<Void> updateLlm(
      @PathVariable("llm_id") @Positive Long llmId,
      @Validated @RequestBody LlmDto.UpdateRequest updateRequest) {
    llmService.updateLlm(
        LlmDto.UpdateRequest.of(
            llmId, updateRequest.getName(), updateRequest.getPricePerToken())
    );
    return ResponseEntity.noContent().build();
  }

  /**
   * LLM 삭제
   *
   * @param llmId LLM 아이디
   * @return 204 No Content 응답
   */
  @DeleteMapping("/llm/{llm_id}")
  @Override
  public ResponseEntity<Void> deleteLlmById(@PathVariable("llm_id") @Positive Long llmId) {
    llmService.deleteLlmById(llmId);
    return ResponseEntity.noContent().build();
  }

  /**
   * LLM별 사용량 조회
   *
   * @return 200 OK 응답과 사용자 목록
   */
  @GetMapping("/llm/usages")
  @Override
  public ResponseEntity<List<LlmUsageDto.StatsResponse>> getLlmUsageStats() {
    return ResponseEntity.ok(llmUsageService.getLlmUsageStats());
  }
}
