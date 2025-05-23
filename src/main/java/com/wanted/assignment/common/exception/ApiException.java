package com.wanted.assignment.common.exception;

import com.wanted.assignment.common.type.ApiStatus;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1728362443478470978L;
  private final HttpStatus httpStatus;
  private final ApiStatus status;

  /**
   * HTTP 상태 코드, Api 상태 코드, 에러 메시지를 받는 ApiException 생성자
   *
   * @param httpStatus HTTP 상태 코드
   * @param apiStatus  Api 상태 코드
   * @param message    에러 메시지
   */
  public ApiException(HttpStatus httpStatus, ApiStatus apiStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
    this.status = apiStatus;
  }

  /**
   * HTTP 상태 코드, Api 상태 코드를 받는 ApiException 생성자
   *
   * @param httpStatus HTTP 상태 코드
   * @param apiStatus  Api 상태 코드
   */
  public ApiException(HttpStatus httpStatus, ApiStatus apiStatus) {
    this(httpStatus, apiStatus, apiStatus.getMessage());
  }

  /**
   * Api 상태 코드, 에러 메시지를 받는 ApiException 생성자
   *
   * @param apiStatus Api 상태 코드
   * @param message   에러 메시지
   */
  public ApiException(ApiStatus apiStatus, String message) {
    this(HttpStatus.INTERNAL_SERVER_ERROR, apiStatus, message);
  }

  /**
   * Api 상태 코드를 받는 ApiException 생성자
   *
   * @param apiStatus Api 상태 코드
   */
  public ApiException(ApiStatus apiStatus) {
    this(HttpStatus.INTERNAL_SERVER_ERROR, apiStatus, apiStatus.getMessage());
  }

  /**
   * 에러 메시지를 받는 ApiException 생성자
   *
   * @param message 에러 메시지
   */
  public ApiException(String message) {
    this(HttpStatus.INTERNAL_SERVER_ERROR, ApiStatus.INTERNAL_SERVER_ERROR, message);
  }
}
