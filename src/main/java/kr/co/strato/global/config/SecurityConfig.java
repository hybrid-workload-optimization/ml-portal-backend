package kr.co.strato.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.strato.global.filter.JwtAuthenticationFilter;


@EnableWebSecurity
public class SecurityConfig {
	
	@Value("${auth.publicKey}")
	private String publicKey;
	
	@Value("${auth.clientId}")
	private String clientId;

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
	    	.authorizeRequests()
	    	.antMatchers("/swagger-ui/**").permitAll()
	    	.antMatchers("/swagger-resources/**").permitAll()
	    	.antMatchers("/v3/api-docs/**").permitAll()
	    	.antMatchers("/error").permitAll()
	    	.antMatchers("/favicon.ico").permitAll()
	    	.antMatchers("/**").authenticated()
	    	.and()
	    	.addFilterBefore(getJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	
	    http.csrf().disable();	
		return http.build();
	}
	
	/**
	 * JWT 검증을 위한 필터 생성하여 반환.
	 * @return
	 */
	public JwtAuthenticationFilter getJwtAuthenticationFilter() {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(publicKey, clientId);
		return jwtAuthenticationFilter;
	}
}
