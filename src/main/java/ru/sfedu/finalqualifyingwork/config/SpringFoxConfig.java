package ru.sfedu.finalqualifyingwork.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class SpringFoxConfig {

  public static final String[] AUTHORIZATION_PATH_EXCLUDE = new String[]{"/api/v1/auth/login",
          "/api/v1/auth/register"};
  @Value("${jwt.header}")
  private static String AUTHORIZATION_HEADER;

  @Bean
  public Docket swaggerSpringfoxDocket() {
    log.debug("Starting Swagger");

    return new Docket(DocumentationType.SWAGGER_2)
            .securityContexts(Collections.singletonList(securityContext()))
            .securitySchemes(Collections.singletonList(apiKey()))
            .pathMapping("/")
            .select()
            .apis(RequestHandlerSelectors.basePackage("ru.sfedu.finalqualifyingwork.rest.api"))
            .paths(PathSelectors.any())
            .build();
  }


  private ApiKey apiKey() {
    return new ApiKey("JWT", "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .operationSelector(operationContext -> Arrays.stream(AUTHORIZATION_PATH_EXCLUDE).map(path ->
                    !operationContext.requestMappingPattern().equals(path)).reduce(true, (a, b) -> a && b))
            .build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Collections.singletonList(
            new SecurityReference("JWT", authorizationScopes));
  }

}
