### 1. `build.gradle` 설정
- Spring Boot Web, AOP, Validation, Security, Test 등 필수 의존성 추가
- JPA, QueryDSL, ModelMapper, Lombok, Swagger 등 DB 연동 및 객체 매핑 관련 의존성 포함
- 정적 분석 도구 사용을 위해 `config` 폴더에 Checkstyle, PMD 설정 XML 파일 추가 및 적용

---

### 2. `application.yml` 구성
- **공통 설정**: 서버 포트, Spring 기본 설정 등 공통 항목 정의
- **환경별 설정 분리**
    - `datasource-local.yml`: DB 및 JPA 관련 설정
    - `environment-local.yml`: H2 콘솔, Swagger 설정

---

### 3. 로그 설정 (`logback-spring.xml`)
- 로그 포맷과 색상 지정
- 파일 로그 없이 콘솔 출력만 수행
- 전체 로그 레벨 `DEBUG` 설정, 주요 패키지는 `DEBUG` 또는 `TRACE` 레벨 적용

---

### 4. 빈 등록 (`config` 패키지)
Spring Boot 기본 자동설정 외 커스텀 구성을 위해 별도 빈 등록
- **DB 설정**: `MainDataSourceConfig`
- **보안 설정**: `security` 패키지 내 설정 클래스
- **ModelMapper 설정**: `ModelMapperConfig`
- **OpenAPI 설정**: `OpenApiConfig`

---

### 5. 요청/응답 로그 등록 (`LoggingFilter`)
- 요청과 응답을 `ContentCachingRequestWrapper`, `ContentCachingResponseWrapper`로 감싸서 필터 체인 실행
- 처리 후 요청/응답 내용을 로그로 출력하고, 응답 바디를 복사해 정상 응답 보장
- **Wrapper 사용 이유**  
  요청과 응답의 바디는 스트림이어서 한 번 읽으면 다시 사용할 수 없는데,  
  이 Wrapper들은 내용을 캐싱해 로그 기록 후에도 정상 처리가 가능하도록 함

---

### 6. ExceptionAdvice
- 전역 예외 처리 (`@RestControllerAdvice`)
- 주요 예외별 JSON 에러 응답 및 로그 출력

---

### 7. @Password
- 비밀번호 검증용 커스텀 어노테이션
- 8~16자, 영문/숫자/특수문자 중 3종 이상 포함 검사

---

### `MainDataSourceConfig` 클래스 어노테이션 정리

#### 클래스 레벨 어노테이션
- `@Configuration`: 설정 클래스 등록, 내부 `@Bean` 메서드로 빈 생성
- `@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})`: 자동설정 활성화하되 DataSource 자동구성 제외
- `@EnableTransactionManagement`: `@Transactional` 선언적 트랜잭션 처리 활성화
- `@EnableJpaRepositories`: JPA 리포지토리 스캔 및 Bean 등록, 다중 데이터소스 지원 위해 EntityManagerFactory, TransactionManager 명시
- `@EnableJpaAuditing`: `@CreatedDate`, `@LastModifiedDate` 필드 자동 처리

#### 메서드/필드 레벨 어노테이션
- `@Qualifier("...")`: 동일 타입 빈 다수일 때 명확한 주입 대상 지정
- `@PersistenceContext`: JPA `EntityManager` 주입, `unitName`으로 특정 PersistenceUnit 바인딩 (다중 데이터소스 환경 유용)

---

### `SecurityConfig` 클래스

#### 보안 설정
- JWT 기반 상태 비저장(stateless) 인증으로 세션 비활성화
- CSRF, HTTP Basic 인증 비활성화
- CORS 설정으로 모든 출처 허용 (개발 환경 기준)
- 인증 없이 접근 가능한 URL 패턴 지정 (예: `/users/**`, `/swagger-ui/**`)
- 요청 로깅용 `LoggingFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 배치
- 인증/인가 실패 시 커스텀 핸들러(`CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`)를 통해 JSON 에러 응답 통일

#### 커스텀 인증/인가 실패 핸들러
- `CustomAuthenticationEntryPoint`: 인증 실패 시 401 상태코드와 JSON 에러 반환
- `CustomAccessDeniedHandler`: 권한 부족 시 403 상태코드와 JSON 에러 반환
- 두 핸들러 모두 로깅 처리하여 문제 추적 용이

#### 비밀번호 암호화
- `BCryptPasswordEncoder` 빈 등록으로 안전한 패스워드 저장 지원
