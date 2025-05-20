package com.wanted.assignment.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  /**
   * 요청 및 응답 내용을 캐싱하여 로그로 기록 후 응답 바디를 복사하여 다시 처리
   *
   * @param request     HTTP 요청 객체
   * @param response    HTTP 응답 객체
   * @param filterChain 필터 체인
   * @throws ServletException 서블릿 예외 발생 시
   * @throws IOException      입출력 예외 발생 시
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      logRequest(requestWrapper);
      logResponse(responseWrapper);
      responseWrapper.copyBodyToResponse();
    }
  }

  /**
   * 요청의 URI 및 바디를 로그에 기록
   *
   * @param requestWrapper HTTP 요청 래핑 객체
   */
  private void logRequest(ContentCachingRequestWrapper requestWrapper) {
    String method = requestWrapper.getMethod();
    String requestUri = requestWrapper.getRequestURI();
    log.debug("Request URI: {} {}", method, requestUri);

    if (!"GET".equalsIgnoreCase(method)) {
      String requestBody = new String(requestWrapper.getContentAsByteArray(),
          StandardCharsets.UTF_8);
      log.debug("Request Body: {}", requestBody);
    }
  }

  /**
   * 응답의 바디를 로그에 기록
   *
   * @param responseWrapper HTTP 응답 래핑 객체
   */
  private void logResponse(ContentCachingResponseWrapper responseWrapper) {
    String responseBody = new String(responseWrapper.getContentAsByteArray(),
        StandardCharsets.UTF_8);
    log.debug("Response Body: {}", responseBody);
  }
}
