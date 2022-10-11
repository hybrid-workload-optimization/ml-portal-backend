package kr.co.strato.global.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTHORIZATION_KEY = "access-token";

	private ApiInfo apiInfo() {
		return new ApiInfo("Machine learning REST API",
				"Interface API - 하이브리드 클라우드 환경 에서 고부하 복합 머신러닝 워크로드의  수행 효율 극대화 를 위한 고집적 연산자원 배치", "v1.0", "",
				new Contact("Lee, HoChul", "", "hclee@strato.co.kr"), 
				"", 
				"", 
				Collections.emptyList());
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(apiInfo())
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey()))
				.select()
				.apis(RequestHandlerSelectors.basePackage("kr.co.strato.portal"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiKey apiKey() {
		return new ApiKey(AUTHORIZATION_KEY, AUTHORIZATION_HEADER, "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference(AUTHORIZATION_KEY, authorizationScopes));
	}

}
