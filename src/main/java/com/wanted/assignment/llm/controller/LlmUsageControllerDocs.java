package com.wanted.assignment.llm.controller;

import com.wanted.assignment.llm.dto.LlmUsageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("all")
@Tag(name = "LLM 호출량 API", description = "LLM (Large Language Model) 호출량 기록 API를 제공")
public interface LlmUsageControllerDocs {

  @Operation(
      summary = "LLM 호출량 기록",
      requestBody = @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LlmUsageDto.InsertRequest.class),
              examples = @ExampleObject(value = """
                {
                  "userId": 1,
                  "llmId": 1,
                  "usedToken": 512
                }
            """)
          )
      ),
      responses = {
          @ApiResponse(responseCode = "201", description = "LLM 사용량 기록 성공")
      }
  )
  ResponseEntity<Void> insertLlmUsage(LlmUsageDto.InsertRequest insertRequest);
}
