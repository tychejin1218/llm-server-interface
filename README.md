# Spring Boot 프로젝트 설정

## 목차

1. [Gradle 설정 (build.gradle)](#1-gradle-설정-buildgradle)
2. [Application 설정 (application.yml)](#2-application-설정-applicationyml)
3. [로그 설정 (logback-spring.xml)](#3-로그-설정-logback-springxml)
4. [패키지 구조](#4-패키지-구조)
5. [config 패키지 Bean 등록](#5-config-패키지-bean-등록)
6. [LoggingFilter](#6-loggingfilter)
7. [ExceptionAdvice](#7-exceptionadvice)
8. [Custom 유효성 검증](#8-custom-유효성-검증)
9. [Entity, Repository 설계](#9-entity-repository-설계)
10. [QueryDSL 설계](#10-querydsl-설계)
11. [DTO 설계](#11-dto-설계)
12. [Service 설계](#12-service-설계)
13. [Controller 설계](#13-controller-설계)
14. [주석 작성](#14-주석-작성)
15. [단위 테스트](#15-단위-테스트)
16. [정적 분석 도구 사용](#16-정적-분석-도구-사용)
17. [MainDataSourceConfig 클래스](#17-maindatasourceconfig-클래스)
18. [SecurityConfig 클래스](#18-securityconfig-클래스)

--- 

## 1. Gradle 설정 (build.gradle)

- Spring Boot Web, AOP, Validation, Security, Test 등 필수 의존성 추가
- JPA, QueryDSL, ModelMapper, Lombok, Swagger 등 DB 연동 및 객체 매핑 관련 의존성 포함
- 정적 분석 도구 사용을 위해 `config` 폴더에 Checkstyle, PMD 설정 XML 파일 추가 및 적용

---

## 2. Application 설정 (application.yml)

- **공통 설정**: 서버 포트, Spring 기본 설정 등 공통 항목 정의
- **환경별 설정 분리**
    - `datasource-local.yml`: DB 및 JPA 관련 설정
    - `environment-local.yml`: H2 콘솔, Swagger 설정

---

## 3. 로그 설정 (logback-spring.xml)

- 로그 포맷과 색상 지정
- 파일 로그 없이 콘솔 출력만 수행
- 전체 로그 레벨 `DEBUG` 설정, 주요 패키지는 `DEBUG` 또는 `TRACE` 레벨 적용

---

## 4. 패키지 구조

- 공통 유틸리티, 상수, Enum 등은 `기본 패키지.common` 하위에 작성
- 빈 설정 등 설정 관련 클래스는 `기본 패키지.config` 하위에 작성
- Entity 클래스는 `기본 패키지.domain.entity` 하위에 작성
- Repository 클래스는 `기본 패키지.domain.repository` 하위에 작성
- Controller, Service, Dto, Custom Repository 등은 각 **도메인별 패키지** 하위에 작성
    - **Controller** 관련 클래스: `기본 패키지.도메인별 패키지.controller`
    - **Service** 관련 클래스: `기본 패키지.도메인별 패키지.service`
    - **Dto** 관련 클래스: `기본 패키지.도메인별 패키지.dto`
    - **Custom Repository** 클래스 및 인터페이스: `기본 패키지.도메인별 패키지.repository`  
      → **QueryDSL** 등 조건이 많거나 동적 SQL 처리가 필요한 경우에 사용

---

## 5. 빈 등록 (`config` 패키지)

Spring Boot 기본 자동설정 외 커스텀 구성을 위해 별도 빈 등록

- **DB 설정**: `MainDataSourceConfig`
- **보안 설정**: `security` 패키지 내 설정 클래스
- **ModelMapper 설정**: `ModelMapperConfig`
- **OpenAPI 설정**: `OpenApiConfig`

---

## 6. 요청/응답 로그 등록 (`LoggingFilter`)

- 요청과 응답을 `ContentCachingRequestWrapper`, `ContentCachingResponseWrapper`로 감싸서 필터 체인 실행
- 처리 후 요청/응답 내용을 로그로 출력하고, 응답 바디를 복사해 정상 응답 보장
- 요청/응답 바디는 InputStream/OutputStream 기반이라 한 번 읽으면 재사용 불가능하므로 Wrapper를 사용해 캐싱 처리
- 민감 정보에 대한 마스킹 처리는 하지 않음

---

## 7. ExceptionAdvice

- `@RestControllerAdvice`를 사용한 전역 예외 처리 컴포넌트
- 관련 클래스 요약
    - **ExceptionAdvice**: 전역에서 발생하는 예외를 처리하여 표준화된 에러 응답을 제공하는 클래스
    - **ErrorResponse**: 클라이언트에 전달할 에러 코드 및 메시지를 담는 응답 DTO
    - **ApiException**: 비즈니스 로직에서 사용되는 커스텀 예외 클래스로 HTTP 상태 및 에러 상태를 포함
    - **ApiStatus**: 에러 코드 및 메시지를 관리하는 enum
    - **ValidationErrorType**: 파라미터 누락 및 유효성 검증 오류 유형을 구분하기 위한 enum (주로
      `MethodArgumentNotValidException` 처리 시 사용)

---

## 8. Custom 유효성 검증

기본 제공되는 어노테이션만으로 검증이 어려워 커스텀 유효성 검증이 필요할 경우, `@interface`로 커스텀 어노테이션을 정의하고, `ConstraintValidator`
구현체에 검증 로직을 작성

**예시:**
`@Password` 어노테이션 정의 → `PasswordValidator`에서 검증 구현 → DTO 필드(예: `UsersDto.InsertRequest.password`)에
적용

---

### 9. Entity, Repository 설계

- 테이블마다 Entity와 기본 Repository를 생성
- **기본 메서드**: `existsById`(엔티티가 존재하는지 확인), `findById`(엔티티 조회) 등 Spring Data JPA가 제공하는 표준 메서드를 사용
- **Query 메서드**: 조건이 있을 때 `existsByName(String name)`과 같이 네이밍 기반 쿼리 메서드를 추가로 정의
- Service 레이어에서는 Entity/Repository의 기본 및 쿼리 메서드를 유효성 체크, 데이터 조회 등 공통 용도로 재사용

**예시:**

- 기본 메서드: `existsById` (예: `LlmRepository.existsById(Long id)`)
- Query 메서드: `existsByName` (예: `LlmRepository.existsByName(String name)`)

---

## 10. QueryDSL 설계

- 기본 Repository의 CRUD/쿼리 메서드로 처리하기 어렵거나, 조건이 복잡한 경우 QueryDSL을 추가로 사용
- **동적 WHERE 절**: `BooleanBuilder`를 사용
- **SELECT 절**: `Projections.fields`를 사용하여 결과를 DTO로 직접 매핑
- **조회 쿼리 주석**: `.setHint` 메서드를 사용해 SQL 주석을 추가

---

## 11. Dto 설계

- 도메인별로 기본 DTO를 작성하고, 기능별로 필요한 DTO는 내부 static 클래스로 추가 작성
- Service 레이어에서는 빌더 패턴 또는 setter 패턴 대신, 팩토리 메서드 패턴(`of` 메서드)을 사용하기 위해 해당 메서드를 제공 (명확성, 가독성)

---

## 12. Service 설계

- 조회 메서드에는 `@Transactional(readOnly = true)`를 사용하고, 그 외의 경우에는 `@Transactional`을 사용
- 비즈니스 로직에서 유효성 검사/예외 발생 시 `ApiException`을 사용하고, 에러 로그를 기록
- Entity와 DTO 변환에는 `ModelMapper`를 사용

---

## 13. Controller 설계

- Swagger 관련 문서는 컨트롤러명+Docs 클래스를 따로 만들어 상속하여 사용
- 응답에는 Http 상태값을 명확히 포함하기 위해 `ResponseEntity`를 사용
- 파라미터 유효성 검사는 기본 제공 어노테이션 및 커스텀 어노테이션을 함께 사용
- 컨트롤러에서는 서비스 계층만 호출하며, 비즈니스 로직은 작성하지 않음

---

## 14. 주석 설정

- 최소 메서드 단위로 JavaDoc 형식의 주석 작성
- 필요에 따라 클래스에도 JavaDoc 작성
- 비즈니스 복잡 구간에는 인라인 주석(//) 사용

---

## 15. 단위 테스트

- Given-When-Then 패턴으로 테스트 메서드를 작성
- 각 메서드 단위로 @Nested 클래스를 사용하여 테스트를 구조화
- 복잡한 비즈니스 로직을 테스트할 때는 @MockitoBean을 사용
- 실제 데이터베이스 상태나 통합 로직이 필요한 경우 @SpringBootTest와 @Transactional를 함께 사용
- 테스트의 목적을 @DisplayName 추가

---

## 16. 정적 분석

- 커밋 전, 프로젝트 빌드를 통해 정적 분석을 확인 후 커밋
- 분석 리포트는 `build/checkstyle-output`, `build/pmd-output` 폴더 하위에서 확인
- 필요 시 검사 예외 처리 및 룰셋을 변경

---

## 17. MainDataSourceConfig 클래스

### 클래스

- `@Configuration`: 설정 클래스 등록, 내부 `@Bean` 메서드로 빈 생성
- `@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})`: 자동설정 활성화하되 DataSource
  자동구성 제외
- `@EnableTransactionManagement`: `@Transactional` 선언적 트랜잭션 처리 활성화
- `@EnableJpaRepositories`: JPA 리포지토리 스캔 및 Bean 등록, 다중 데이터소스 지원 위해 EntityManagerFactory,
  TransactionManager 명시
- `@EnableJpaAuditing`: `@CreatedDate`, `@LastModifiedDate` 필드 자동 처리

### 필드 및 메서드

- `@Qualifier("...")`: 동일 타입 빈 다수일 때 명확한 주입 대상 지정
- `@PersistenceContext`: JPA `EntityManager` 주입, `unitName`으로 특정 PersistenceUnit 바인딩 (다중 데이터소스 환경
  유용)

---

## 18. SecurityConfig 클래스

#### 인증 관련

- JWT 기반 상태 비저장(stateless) 인증으로 세션 비활성화
- CSRF, HTTP Basic 인증 비활성화
- CORS 설정으로 모든 출처 허용 (개발 환경 기준)
- 인증 없이 접근 가능한 URL 패턴 지정 (예: `/users/**`, `/swagger-ui/**`)
- 요청 로깅용 `LoggingFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 배치
- 인증/인가 실패 시 커스텀 핸들러(`CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`)를 통해 JSON 에러 응답
  통일

### 필터 관련

- `CustomAuthenticationEntryPoint`: 인증 실패 시 401 상태코드와 JSON 에러 반환
- `CustomAccessDeniedHandler`: 권한 부족 시 403 상태코드와 JSON 에러 반환
- 두 핸들러 모두 로깅 처리하여 문제 추적 용이

### 비밀번호 암호화

- `BCryptPasswordEncoder` 빈 등록으로 안전한 패스워드 저장 지원
