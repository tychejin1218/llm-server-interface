package com.wanted.assignment.llm.controller;

import com.wanted.assignment.common.response.ErrorResponse;
import com.wanted.assignment.llm.dto.LlmDto;
import com.wanted.assignment.llm.dto.LlmUsageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@SuppressWarnings("all")
@Tag(name = "LLM API", description = "LLM (Large Language Model) 추가, 조회, 수정 API를 제공")
public interface LlmControllerDocs {

  @Operation(
      summary = "LLM 추가",
      requestBody = @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LlmDto.InsertRequest.class),
              examples = @ExampleObject(value = """
                      {
                        "name": "gpt-4o",
                        "pricePerToken": 20
                      }
                  """)
          )
      ),
      responses = {
          @ApiResponse(responseCode = "201", description = "LLM 추가 성공"),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 정보입니다.",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))
          )
      }
  )
  ResponseEntity<Void> insertLlm(LlmDto.InsertRequest insertRequest);

  @Operation(
      summary = "LLM 목록 조회",
      parameters = {
          @Parameter(name = "name", description = "LLM 이름으로 필터링 (선택)", example = "gpt")
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "LLM 목록 조회 성공",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = LlmDto.SelectResponse.class)),
                  examples = @ExampleObject(value = """
                          [
                            {"id": 1, "name": "gpt-4o", "pricePerToken": 20},
                            {"id": 2, "name": "gpt-3.5-turbo", "pricePerToken": 10}
                          ]
                      """)
              )
          )
      }
  )
  ResponseEntity<List<LlmDto.SelectResponse>> getLlmList(String name);

  @Operation(
      summary = "LLM 수정",
      requestBody = @RequestBody(
          required = true,
          content = @Content(
              schema = @Schema(implementation = LlmDto.UpdateRequest.class),
              examples = @ExampleObject(value = """
                      {
                        "name": "gpt-4o-mini",
                        "pricePerToken": 15
                      }
                  """)
          )
      ),
      responses = {
          @ApiResponse(responseCode = "204", description = "LLM 수정 성공"),
          @ApiResponse(responseCode = "404", description = "존재하지 않는 LLM",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                  examples = @ExampleObject(value = """
                          {
                            "code": "LLM_NOT_FOUND",
                            "message": "LLM이 존재하지 않습니다."
                          }
                      """)
              )
          )
      }
  )
  ResponseEntity<Void> updateLlm(
      @Positive @Parameter(name = "llm_id", description = "수정할 LLM ID", required = true, example = "1") Long llmId,
      @Validated @RequestBody LlmDto.UpdateRequest updateRequest);

  @Operation(
      summary = "LLM 삭제",
      responses = {
          @ApiResponse(responseCode = "204", description = "LLM 삭제 성공"),
          @ApiResponse(responseCode = "404", description = "존재하지 않는 LLM",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                  examples = @ExampleObject(value = """
                          {
                            "code": "LLM_NOT_FOUND",
                            "message": "LLM이 존재하지 않습니다."
                          }
                      """)
              )
          )
      }
  )
  ResponseEntity<Void> deleteLlmById(
      @Positive @Parameter(name = "llm_id", description = "삭제할 LLM ID", required = true, example = "1") Long llmId);

  @Operation(
      summary = "LLM별 사용량 통계 조회",
      responses = {
          @ApiResponse(responseCode = "200", description = "LLM 사용량 통계 조회 성공",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = LlmUsageDto.StatsResponse.class)),
                  examples = @ExampleObject(value = """
                          [
                            {"id": 1, "name": "gpt-4o-mini", "totalUsedToken": 1536, "totalPrice": 15360},
                            {"id": 2, "name": "gpt-4o", "totalUsedToken": 3072, "totalPrice": 61440},
                            {"id": 3, "name": "gpt-3.5-turbo", "totalUsedToken": 6144, "totalPrice": 184320}
                          ]
                      """)
              )
          )
      }
  )
  ResponseEntity<List<LlmUsageDto.StatsResponse>> getLlmUsageStats();
}
