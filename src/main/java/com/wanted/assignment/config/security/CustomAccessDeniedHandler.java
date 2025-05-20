package com.wanted.assignment.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.assignment.common.response.ErrorResponse;
import com.wanted.assignment.common.type.ApiStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  /**
   * 권한 거부 시 호출되는 메서드
   *
   * @param request               HttpServletRequest 객체
   * @param response              HttpServletResponse 객체
   * @param accessDeniedException 권한 거부 예외 객체
   * @throws IOException 입출력 예외
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    log.error("No Authorization Request URI: {}", request.getRequestURI(), accessDeniedException);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), ErrorResponse.builder()
        .code(ApiStatus.UNAUTHORIZED.getCode())
        .message(ApiStatus.UNAUTHORIZED.getMessage())
        .build());
  }
}
