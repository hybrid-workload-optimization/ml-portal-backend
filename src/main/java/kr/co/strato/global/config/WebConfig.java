package kr.co.strato.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.strato.global.interceptor.AccessInterceptor;


/*
타 제품에서 CoMP 호출 시 로그인한 사용자가 CoMP 사용권한이 없을 경우를 대비해 CoMP 권한 채크는 제외함.
2023.04.07 이호철
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authCheckInterceptor())
				.order(1) // 인터셉터 체인 순서
				.addPathPatterns("/**") // 모든 requestURL에 대해 적용
				.excludePathPatterns(
						"/",
						"/login", 
						"/logout", 
						"/css/**", 
						"/*.ico", 
						"/error",
						"/pass",
						"/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
	}
	
	@Bean
    public AccessInterceptor authCheckInterceptor() {
        return new AccessInterceptor();
    }
}
