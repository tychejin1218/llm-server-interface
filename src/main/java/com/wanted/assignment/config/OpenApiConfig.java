package com.wanted.assignment.config;

import com.wanted.assignment.common.constants.Constants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  /**
   * OpenAPI 설정
   *
   * @return OpenAPI 객체
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info()
            //.title("[원티드랩] 자바 개발자 과제")
            .title("123")
            .description("사용자(User) 및 LLM(Large Language Model) 관련 API 제공")
            .version("1.0.0"))
        .addServersItem(new Server().url("/"));
  }

  @Bean
  public GroupedOpenApi usersApi() {
    return GroupedOpenApi.builder()
        .group("사용자 관련 API")
        .pathsToMatch("/users/**")
        .packagesToScan(Constants.BASE_PACKAGE + ".users.controller")
        .build();
  }

  @Bean
  public GroupedOpenApi llmApi() {
    return GroupedOpenApi.builder()
        .group("LLM 관련 API")
        .pathsToMatch("/llm/**")
        .packagesToScan(Constants.BASE_PACKAGE + ".llm.controller")
        .build();
  }

  @Bean
  public GroupedOpenApi llmUsagesApi() {
    return GroupedOpenApi.builder()
        .group("LLM 호출량 관련 API")
        .pathsToMatch("/usages/**")
        .packagesToScan(Constants.BASE_PACKAGE + ".llm.controller")
        .build();
  }
}
