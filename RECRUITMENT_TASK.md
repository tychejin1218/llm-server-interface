## 주의사항

- 과제의 1차 검증을 위해 자체 툴로 자동 테스트를 진행하고 있습니다.  
  따라서 Request, Response 타입을 요구 사항과 동일하게 작성해주실 것을 부탁드립니다.

---

```
서버를 만드는 과제입니다.
아래 기능적 요구 사항과 비기능적 요구 사항을 잘 확인하고 제출 부탁드립니다.
```

## 비기능적 요구 사항

* 데이터베이스는 H2를 사용해서 개발해주세요.
* 서버 호스트는 localhost(127.0.0.1), 서버 포트는 8080으로 설정해주세요.
* 가독성 좋은 코드로 개발해주세요.
* 개발 언어는 [Java(21 이상)](https://www.java.com/)를 사용해서 개발해주세요.
* [Spring Boot 3.x](https://spring.io/projects/spring-boot) 이상 버전을 사용해서 개발해주세요.
* Persistence Framework는 JPA(with QueryDSL) 또는 MyBatis 둘 중 하나를 택1 하여 사용해주세요.
* 모든 Request와 Response는 로그로 남겨주세요.
* 최종 완성된 DB Schema는 Git Repository에 첨부해주세요.
* API당 최소한 하나 이상의 성공/실패 케이스 테스트 코드를 작성해주세요.

## 기능적 요구 사항

### 공통

- 아래에 정의된 API 형식을 그대로 사용해서 개발해주세요.
- 정상적으로 처리한 요청에 대해 응답할 땐 아래와 같은 `Response Body` 형식을 사용합니다.

```json
// 200 OK
object or empty body
```

ex)

```json
// 200 OK
{
  "companyName": "WantedLab"
}
```

```json
// 200 OK
[
  "W",
  "A",
  "N",
  "T",
  "E",
  "D",
  "L",
  "A",
  "B"
]
```

```json
// 204 NO_CONTENT
```

- 필수 여부가 `required` 임에도 존재하지 않거나 조건을 지키지 못했을 경우 아래와 같은 `Response Body` 형식을 사용합니다.

```json
// 400 BAD_REQUEST
{
  "code": "BAD_REQUEST_BODY",
  "message": "자유롭게 작성"
}
```

### 1. 사용자 추가 API: POST /users

- Request Body

  ```json
  {
    "name": "wan티드01",
    "email": "ai@wantedlab.com",
    "password": "1q2w3e4r!"
  }
  ```

  |    이름    |       설명       |  필수 여부   |   타입   |                           조건                           |
  |:--------:|:--------------:|:--------:|:------:|:------------------------------------------------------:|
  |   name   |   추가할 사용자 이름   | required | String |                 1~16자, 한글/영어/숫자 포함 가능                  |
  |  email   | 추가할 사용자 이메일 주소 | required | String |                     이메일 형식 반드시 준수                      |
  | password |   추가할 사용자 암호   | required | String |           8~16자, 영문 대소문자, 숫자, 특수문자 3가지 이상 조합           |

- Response Body

  ```json
  // 201 CREATED
  ```

- 예외처리
  - 중복된 이메일로 요청할 경우 아래와 같이 응답합니다.
    ```json
    // 400 BAD_REQUEST
    {
      "code" : "BAD_REQUEST_BODY",
      "message" : "자유롭게 작성"
    }
    ```

- 주의사항
  - 사용자 추가시 이미 존재하는지 확인하는 식별자는 email이지만 실제로는 Number 타입의 식별자를 따로 두어야 합니다.
    또한 식별자는 사용자 추가시 자동 생성되어야 합니다.

### 2. 사용자 목록 조회 API: GET /users

- Request Body
  ```json

  ```
- Response Body
  ```json
  [
    {"id": 1, "name": "김티드", "email": "kimted@wantedlab.com"},
    {"id": 2, "name": "지티드", "email": "jited@wantedlab.com"},
    {"id": 3, "name": "이티드", "email": "leeted@wantedlab.com"}
  ]
  ```

- 예외 처리

- 주의사항
  - 저장된 사용자가 없는 경우엔 빈배열 응답

### 3. 사용자 삭제 API: DELETE /users/{user_id}

- Request Body
  ```json

  ```
  |  이름   |     설명     |  필수 여부   |   타입   | 조건 |
  |:-----:|:----------:|:--------:|:------:|:--:|
  | user_id  | 삭제할 사용자 ID | required | Number | -  |

- Response Body
  ```json
    // 204 NO_CONTENT
  ```
- 예외 처리

- 주의사항

### 4. LLM 추가 API: POST /llm

- Request Body
  ```json
  {
    "name": "gpt-4o-mini",
    "pricePerToken" : 10
  }
  ```
  |      이름       |       설명       |  필수 여부   |   타입   |  조건   |
  |:-------------:|:--------------:|:--------:|:------:|:-----:|
  |     name      |   추가할 LLM 이름   | required | String | 1~20자 |
  | pricePerToken | 추가할 LLM 토큰당 가격 | required | Number |  양수   |

- Response Body
  ```json
  // 201 CREATED
  ```

- 예외 처리
  - 중복된 name으로 요청할 경우 아래와 같이 응답합니다.
      ```json
      // 400 BAD_REQUEST
      {
        "code" : "BAD_REQUEST_BODY",
        "message" : "자유롭게 작성"
      }
      ```

- 주의사항
  - LLM 추가시 이미 존재하는지 확인하는 식별자는 name이지만 실제로는 Number 타입의 식별자를 따로 두어야 합니다.
    또한 식별자는 LLM 추가시 자동 생성되어야 합니다.

### 5. LLM 목록 조회 API: GET /llm

- Request Body
  ```json

  ```
- Response Body
  ```json
  [
    {"id": 1, "name": "gpt-4o-mini", "pricePerToken": 10},
    {"id": 2, "name": "gpt-4o", "pricePerToken": 20},
    {"id": 3, "name": "gpt-4o-turbo", "pricePerToken": 30}
  ]
  ```
- 예외 처리

- 주의사항

### 6. LLM 수정 API: PUT /llm/{llm_id}

- Request Body
  ```json
  {
    "name": "gpt-4",
    "pricePerToken": 5
  }
  ```
  |      이름       |       설명       |  필수 여부   |   타입   |  조건   |
  |:-------------:|:--------------:|:--------:|:------:|:-----:|
  |    llm_id     |   수정할 LLM ID   | required | Number |   -   |
  |     name      |   수정할 LLM 이름   | required | String | 1~20자 |
  | pricePerToken | 수정할 LLM 토큰당 가격 | required | Number |  양수   |

- Response Body
  ```json
  // 204 NO CONTENT
  ```
- 예외 처리

- 주의사항

### 7. LLM 삭제 API: DELETE /llm/{llm_id}

- Request Body
  ```json

  ```
  |      이름       |       설명       |  필수 여부   |   타입   |          조건           |
  |:-------------:|:--------------:|:--------:|:------:|:---------------------:|
  |    llm_id     |   수정할 LLM ID   | required | Number |           -           |

- Response Body
  ```json
  // 204 NO CONTENT
  ```
- 예외 처리

- 주의사항
  - 해당 LLM이 삭제 될 때 사용량도 같이 삭제되도록 구현

### 8. LLM 호출량 기록 API: POST /usages

- Request Body
  ```json
  {
    "userId": 1,
    "llmId": 1,
    "usedToken" : 512
  }
  ```
- Response Body
  ```json
  // 201 CREATED
  ```

- 예외 처리

- 주의사항

### 9. 특정 사용자 LLM 사용량 조회 API GET: /users/{user_id}/usages

- Request Body
  ```json
  
  ```
  |      이름       |       설명        |  필수 여부   |   타입   |          조건           |
  |:-------------:|:---------------:|:--------:|:------:|:---------------------:|
  |    user_id     | 사용량을 조회할 사용자 ID | required | Number |           -           |

- Response Body
  ```json
  // 200 OK
  {
    "userUsages" : {
      "totalUsedToken" : 1536,
      "totalPrice" : 30720
    },
    "llmUsages" : [
      {"id": 1, "name" : "gpt-4o-mini", "totalUsedToken" : 512, "totalPrice" : 5120},
      {"id": 2, "name" : "gpt-4o", "totalUsedToken" : 512, "totalPrice" : 10240},
      {"id": 3, "name" : "gpt-3.5-turbo", "totalUsedToken" : 512, "totalPrice" : 15360}
    ]
  }
  ```
- 예외 처리
  - 존재하지 않는 user_id로 조회시 아래와 같이 응답하도록 개발
    ```json
    // 404 NOT FOUND
    {
      "code": "USER_NOT_FOUND",
      "message": "자유롭게 작성"
    }
    ```

- 주의사항
  - llm 사용량이 존재하지 않을 경우 userUsages.totalUsedToken, userUsages.totalPrice 는 각각 0으로 응답하고
    llmUsages는 빈배열 응답하도록 개발

### 10. LLM별 사용량 조회 API GET: /llm/usages

- Request Body
  ```json

  ```
- Response Body
  ```json
  // 200 OK
  [
    {"id": 1, "name": "gpt-4o-mini", "totalUsedToken": 1536, "totalPrice": 15360},
    {"id": 2, "name": "gpt-4o", "totalUsedToken": 3072, "totalPrice": 61440},
    {"id": 3, "name": "gpt-3.5-turbo", "totalUsedToken": 6144, "totalPrice": 184320}
  ]  
  ```
- 예외 처리

- 주의사항
  - 사용량이 존재하지 않는 llm의 totalUsedToken, totalPrice 는 0으로 응답하도록 개발

## 실행  
./gradlew llm-server-interface:bootRun  
