# [원티드랩] 자바 개발자 과제

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [API 개요](#api-개요)
4. [정적 분석 도구](#정적-분석-도구)
5. [DB 스키마](#db-스키마)
6. [H2 Console 접속 경로](#h2-console-접속-경로)
7. [Swagger UI 접속 경로](#swagger-ui-접속-경로)
8. [Request와 Response 로그](#request와-response-로그)
9. [예외 처리 방식](#예외-처리-방식)

---

## 프로젝트 개요
이 프로젝트는 LLM(대규모 언어 모델)의 사용량을 관리하고 사용자별 데이터 저장/조회 기능을 구현한 과제입니다.  
Spring Boot 기반으로 설계하였으며, 데이터는 In-Memory H2 Database를 사용하여 가볍게 관리합니다. 또한 API 테스트를 위해 Swagger UI를 제공합니다.

---

## 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.4.5
- **빌드 도구**: Gradle 8.9
- **DB**: H2 Database (In-Memory)
- **ORM**: JPA with QueryDSL
- **사용 라이브러리**: Spring Web, Spring Data JPA, Security, Validation, ModelMapper, Lombok
- **테스트 프레임워크**: JUnit 5, Mockito

---

## API 개요

### 사용자 관리
- **사용자 추가**: `POST /users`
- **사용자 목록 조회**: `GET /users`
- **사용자 삭제**: `DELETE /users/{user_id}`
- **사용자별 사용량 조회**: `GET /users/{user_id}/usages`

### LLM 관리
- **LLM 추가**: `POST /llm`
- **LLM 목록 조회**: `GET /llm`
- **LLM 수정**: `PUT /llm/{llm_id}`
- **LLM 삭제**: `DELETE /llm/{llm_id}`
- **LLM별 사용량 조회**: `GET /llm/usages`

### LLM 호출량 관리
- **LLM 사용량 기록**: `POST /usages`

---

## 정적 분석 도구

- **Checkstyle**: 코드 스타일 유지를 위한 정적 코드 분석 도구
- **PMD**: 코드 품질 유지 및 코드 스타일 규칙 적용을 위한 정적 코드 분석 도구

---

## DB 스키마

- [DB Schema](https://github.com/wanteddev/20250503_tychejin/blob/main/llm-server-interface/src/main/resources/script/schema.sql)

### 주요 테이블
- **users**: 사용자 정보를 저장
- **llm**: LLM 정보를 저장
- **llm_usage**: LLM 사용량 정보를 저장

---

## H2 Console 접속 경로

- [H2 Console](http://localhost:8080/h2-console)

---

## Swagger UI 접속 경로

- [Swagger UI](http://localhost:8080/swagger-ui.html)


---

## Request와 Response 로그

### 로깅 필터 클래스
- **클래스 이름**: `com.wanted.assignment.config.security.LoggingFilter`
- **기능**:
    - 요청 시 URI, 메소드, 바디를 기록
    - 응답 시 상태 코드와 바디 기록

### 로그 예시
**Request/Response 로그**:
```plaintext
Request URI: GET /users
Response Body: [{"id":1,"name":"김티드","email":"kimted@wantedlab.com"},{"id":2,"name":"지티드","email":"jited@wantedlab.com"},{"id":3,"name":"이티드","email":"leeted@wantedlab.com"}]
```

---

## 예외 처리 방식

- 애플리케이션 전반에서 발생하는 예외는 `@ControllerAdvice`와 `@ExceptionHandler`를 사용하여 처리합니다.
- 모든 예외 정보를 클라이언트에게 JSON 형식으로 반환합니다.

### 예외 응답 형식
```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

### 예시
1. **요청 값 검증 실패**:
    - **400 BAD_REQUEST**
    - Response:
      ```json
      {
        "code": "BAD_REQUEST",
        "message": "유효하지 않은 요청 정보입니다."
      }
      ```

2. **사용자 조회 실패**:
    - **404 NOT_FOUND**
    - Response:
      ```json
      {
        "code": "USER_NOT_FOUND",
        "message": "사용자가 존재하지 않습니다."
      }
      ```
