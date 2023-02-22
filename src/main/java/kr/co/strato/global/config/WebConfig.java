package kr.co.strato.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.strato.global.interceptor.AccessInterceptor;



@Configuration
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
						"/pass");
	}
	
	@Bean
    public AccessInterceptor authCheckInterceptor() {
        return new AccessInterceptor();
    }
}
