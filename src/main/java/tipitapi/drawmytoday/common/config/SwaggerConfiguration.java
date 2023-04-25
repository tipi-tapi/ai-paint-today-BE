package tipitapi.drawmytoday.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "오늘 하루를 그려줘 API 문서",
        description = "프로그라피 8기 4팀 TipiTapi의 오늘 하루를 그려줘 프로젝트의 API 문서입니다.",
        version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfiguration {

  @Bean
  public GroupedOpenApi usersOpenApi() {
    String[] paths = {"/users/**"};

    return GroupedOpenApi
        .builder()
        .group("유저 API")
        .pathsToMatch(paths)
        .addOpenApiCustomiser(buildSecurityOpenApi()).build();
  }

  @Bean
  public GroupedOpenApi diaryOpenAPi() {
    String[] paths = {"/diary/**"};

    return GroupedOpenApi
        .builder()
        .group("일기 API")
        .pathsToMatch(paths)
        .addOpenApiCustomiser(buildSecurityOpenApi()).build();
  }

  @Bean
  public GroupedOpenApi emotionsOpenAPi() {
    String[] paths = {"/emotions/**"};

    return GroupedOpenApi
        .builder()
        .group("감정 API")
        .pathsToMatch(paths)
        .addOpenApiCustomiser(buildSecurityOpenApi()).build();
  }

  public OpenApiCustomiser buildSecurityOpenApi() {
    // jwt token 을 한번 설정하면 header 에 값을 넣어주는 코드
    return OpenApi -> OpenApi.addSecurityItem(new SecurityRequirement().addList("jwt token"))
        .getComponents().addSecuritySchemes("jwt token", new SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .in(SecurityScheme.In.HEADER)
            .bearerFormat("JWT")
            .scheme("bearer"));
  }
}