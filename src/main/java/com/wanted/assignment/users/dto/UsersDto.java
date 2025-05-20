package com.wanted.assignment.users.dto;

import com.wanted.assignment.common.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class UsersDto {

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class InsertRequest {

    @Schema(description = "추가할 사용자 이름", example = "wan티드01",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 1, max = 16)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$")
    private String name;

    @Schema(description = "추가할 사용자 이메일 주소", example = "ai@wantedlab.com",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "추가할 사용자 암호", example = "1q2w3e4r!",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @Password
    private String password;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class SelectRequest {

    @Schema(description = "사용자 이름", example = "김티드")
    private String name;

    @Schema(description = "사용자 이메일", example = "kimted@wantedlab.com")
    private String email;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class SelectResponse {

    @Schema(description = "사용자 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "사용자 이름", example = "김티드",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용자 이메일", example = "kimted@wantedlab.com",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class LlmResponse {

    @Schema(description = "사용자 전체 사용량 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserUsage userUsages;

    @Schema(description = "LLM별 사용량 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<LlmUsage> llmUsages;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class UserUsage {

    @Schema(description = "총 사용 토큰 수", example = "1536",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalUsedToken;

    @Schema(description = "총 사용 금액", example = "30720",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalPrice;
  }

  @Getter
  @Builder
  @AllArgsConstructor(staticName = "of")
  @NoArgsConstructor
  @ToString
  public static class LlmUsage {

    @Schema(description = "LLM 아이디", example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "LLM 이름", example = "gpt-4o-mini",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "해당 LLM 총 사용 토큰 수", example = "512",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalUsedToken;

    @Schema(description = "해당 LLM 총 사용 금액", example = "5120",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalPrice;
  }
}
