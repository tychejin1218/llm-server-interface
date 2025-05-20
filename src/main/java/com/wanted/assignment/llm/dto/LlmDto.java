package com.wanted.assignment.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class LlmDto {

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class InsertRequest {

    @Schema(description = "추가할 LLM 이름", example = "gpt-4o-mini",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    @Schema(description = "추가할 LLM 토큰당 가격", example = "10",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive
    private int pricePerToken;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class UpdateRequest {

    @Schema(description = "수정할 LLM ID", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "수정할 LLM 이름", example = "gpt-4o-mini",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    @Schema(description = "수정할 LLM 토큰당 가격", example = "10",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private int pricePerToken;

    public static UpdateRequest of(String name, int pricePerToken) {
      return UpdateRequest.builder()
          .name(name)
          .pricePerToken(pricePerToken)
          .build();
    }

    public static UpdateRequest of(long id, String name, int pricePerToken) {
      return UpdateRequest.builder()
          .id(id)
          .name(name)
          .pricePerToken(pricePerToken)
          .build();
    }
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SelectRequest {

    private String name;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SelectResponse {

    @Schema(description = "LLM 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "LLM 이름", example = "gpt-4o-mini",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "토큰당 가격", example = "10",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private int pricePerToken;
  }
}
