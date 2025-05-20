package com.wanted.assignment.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class LlmUsageDto {

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class InsertRequest {

    @Schema(description = "사용자 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long userId;

    @Schema(description = "LLM 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long llmId;

    @Schema(description = "사용 토큰 수", example = "512",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(1)
    private Integer usedToken;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class StatsResponse {

    @Schema(description = "LLM 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "LLM 이름", example = "gpt-4o-mini",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "총 사용 토큰 수", example = "1536",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalUsedToken;

    @Schema(description = "총 사용 금액", example = "15360",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalPrice;
  }
}
