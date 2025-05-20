package com.wanted.assignment.common.response;

import com.wanted.assignment.common.type.ApiStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class ErrorResponse {

  @Schema(description = "에러 코드", example = "BAD_REQUEST_BODY",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "에러 메시지", example = "유효하지 않은 요청 정보입니다.",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String message;

  /**
   * 에러 응답 객체 생성
   *
   * @param code    에러 코드
   * @param message 에러 메시지
   */
  @Builder
  ErrorResponse(
      String code,
      String message
  ) {
    this.code = code;
    this.message = StringUtils.hasText(message) ? message : ApiStatus.getByCode(code).getMessage();
  }
}
