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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * 인증 실패 시 호출되는 메서드
   *
   * @param request       HttpServletRequest 객체
   * @param response      HttpServletResponse 객체
   * @param authException 인증 예외 객체
   * @throws IOException 입출력 예외
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    log.error("Not Authentication Request URI: {}", request.getRequestURI(), authException);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), ErrorResponse.builder()
        .code(ApiStatus.UNAUTHORIZED.getCode())
        .message(ApiStatus.UNAUTHORIZED.getMessage())
        .build());
  }
}
