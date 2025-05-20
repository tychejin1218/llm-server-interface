package com.wanted.assignment.users.controller;

import com.wanted.assignment.common.response.ErrorResponse;
import com.wanted.assignment.users.dto.UsersDto;
import com.wanted.assignment.users.dto.UsersDto.LlmResponse;
import com.wanted.assignment.users.dto.UsersDto.SelectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("all")
@Tag(name = "사용자 API", description = "사용자 추가, 조회, 삭제, LLM 사용량 API를 제공)")
public interface UserControllerDocs {

  @Operation(
      summary = "사용자 추가",
      requestBody = @RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UsersDto.InsertRequest.class),
              examples = @ExampleObject(value = """
                    {
                      "name": "wan티드01",
                      "email": "ai@wantedlab.com",
                      "password": "1q2w3e4r!"
                    }
                """)
          )
      ),
      responses = {
          @ApiResponse(responseCode = "201", description = "사용자 추가 성공"),
          @ApiResponse(
              responseCode = "400", description = "유효하지 않은 요청",
              content = @Content(
                  schema = @Schema(implementation = ErrorResponse.class),
                  examples = @ExampleObject(value = """
                        {
                          "code": "BAD_REQUEST_BODY",
                          "message": "유효하지 않은 요청 정보입니다."
                        }
                    """)
              )
          )
      }
  )
  ResponseEntity<Void> insertUser(UsersDto.InsertRequest insertRequest);

  @Operation(
      summary = "사용자 목록 조회",
      parameters = {
          @Parameter(name = "name", description = "사용자명", example = "김티드"),
          @Parameter(name = "email", description = "사용자 이메일", example = "kimted@wantedlab.com")
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
              content = @Content(array = @ArraySchema(schema = @Schema(
                  example = """
                        [
                          {"id": 1, "name": "김티드", "email": "kimted@wantedlab.com"},
                          {"id": 2, "name": "지티드", "email": "jited@wantedlab.com"},
                          {"id": 3, "name": "이티드", "email": "leeted@wantedlab.com"}
                        ]
                    """
              )))
          )
      }
  )
  ResponseEntity<List<SelectResponse>> getUserList(String name, String email);

  @Operation(
      summary = "사용자 삭제",
      responses = {
          @ApiResponse(responseCode = "204", description = "사용자 삭제 성공")
      }
  )
  ResponseEntity<Void> deleteUserById(@Positive @Parameter(name = "user_id", description = "삭제할 사용자 ID", required = true, example = "1") Long userId);

  @Operation(
      summary = "사용자 사용량 조회",
      parameters = {
          @Parameter(name = "userId", description = "사용량을 조회할 사용자 ID", required = true, example = "1")
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "사용자 사용량 조회 성공",
              content = @Content(schema = @Schema(implementation = LlmResponse.class),
                  examples = @ExampleObject(value = """
                        {
                          "userUsages": {
                            "totalUsedToken": 1536,
                            "totalPrice": 30720
                          },
                          "llmUsages": [
                            {"id": 1, "name": "gpt-4o-mini", "totalUsedToken": 512, "totalPrice": 5120},
                            {"id": 2, "name": "gpt-4o", "totalUsedToken": 512, "totalPrice": 10240},
                            {"id": 3, "name": "gpt-3.5-turbo", "totalUsedToken": 512, "totalPrice": 15360}
                          ]
                        }
                    """)
              )
          ),
          @ApiResponse(responseCode = "404", description = "사용자 없음",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                  examples = @ExampleObject(value = """
                        {
                          "code": "USER_NOT_FOUND",
                          "message": "해당 사용자가 존재하지 않습니다."
                        }
                    """)
              )
          )
      }
  )
  ResponseEntity<LlmResponse> getUserLlmResponse(
      @Positive @Parameter(name = "user_id", description = "특정 사용자 ID", required = true, example = "1") Long userId);
}
