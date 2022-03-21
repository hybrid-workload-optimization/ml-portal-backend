package kr.co.strato.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.strato.global.interceptor.AccessInterceptor;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AccessInterceptor())
			.addPathPatterns("/*")
			.excludePathPatterns("/login");
	}

}
