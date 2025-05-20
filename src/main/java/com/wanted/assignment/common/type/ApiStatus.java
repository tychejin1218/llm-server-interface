package com.wanted.assignment.common.type;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiStatus {

  // 성공 코드
  OK("OK", "성공"),

  // Custom Exception 에러 코드
  BAD_REQUEST_BODY("BAD_REQUEST_BODY", "유효하지 않은 요청 정보입니다."),
  UNAUTHORIZED("UNAUTHORIZED", "유효하지 않은 권한입니다."),
  USER_NOT_FOUND("USER_NOT_FOUND", "사용자가 존재하지 않습니다."),
  LLM_NOT_FOUND("LLM_NOT_FOUND", "LLM이 존재하지 않습니다."),

  // Exception Handler 에러 코드
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "내부 오류가 발생했습니다. 확인 후 다시 시도해주세요."),
  FORBIDDEN_REQUEST("FORBIDDEN_REQUEST", "접근 권한이 없습니다."),
  METHOD_ARGUMENT_NOT_VALID("METHOD_ARGUMENT_NOT_VALID", "파라미터가 유효하지 않습니다."),
  MISSING_SERVLET_REQUEST_PARAMETER("MISSING_SERVLET_REQUEST_PARAMETER",
      "필수 파라미터가 누락되었습니다."),
  CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "파라미터 유효성 검사에 실패했습니다."),
  METHOD_ARGUMENT_TYPE_MISMATCH("METHOD_ARGUMENT_TYPE_MISMATCH", "파라미터 타입이 올바르지 않습니다."),
  NO_HANDLER_FOUND("NO_HANDLER_FOUND", "요청한 URL을 찾을 수 없습니다."),
  NO_RESOURCE_FOUND("NO_RESOURCE_FOUND", "리소스를 찾을 수 없습니다."),
  HTTP_REQUEST_METHOD_NOT_SUPPORTED("HTTP_REQUEST_METHOD_NOT_SUPPORTED", "지원하지 않는 메서드입니다."),
  HTTP_MEDIA_TYPE_NOT_SUPPORTED("HTTP_MEDIA_TYPE_NOT_SUPPORTED", "지원되지 않는 미디어 타입입니다."),
  HTTP_MESSAGE_NOT_READABLE_EXCEPTION("HTTP_MESSAGE_NOT_READABLE_EXCEPTION",
      "읽을 수 있는 요청 정보가 없습니다.");

  private final String code;
  private final String message;

  private static final Map<String, ApiStatus> BY_CODE =
      Stream.of(values())
          .collect(Collectors.toMap(ApiStatus::getCode, Function.identity()));

  public static ApiStatus getByCode(String code) {
    return BY_CODE.get(code);
  }
}
