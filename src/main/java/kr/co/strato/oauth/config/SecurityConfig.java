package kr.co.strato.oauth.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.strato.oauth.config.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import kr.co.strato.oauth.config.oauth.OAuth2AuthenticationFailureHandler;
import kr.co.strato.oauth.config.oauth.OAuth2AuthenticationSuccessHandler;
import kr.co.strato.oauth.filter.JwtAuthenticationFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    
    @Value("${auth.jwt.publicKey}")
	private String publicKey;
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
	@Value("${auth.apiToken}")
	private String apiToken;
	
	@Value("${ml.api.token}")
	private String mlToken;
    

    public SecurityConfig(OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
	        .cors(cors -> cors
					.disable()
			)
			.csrf(csrf -> csrf
					.disable()	
			)
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.formLogin(login -> login
					.disable()
			)
			.httpBasic(h -> h
					.disable()
			)
			.authorizeRequests(request -> request
					.antMatchers("/auth/login","/auth/refresh_token", "/auth/logout", "/error", "/favicon.ico").permitAll()
					.antMatchers("/token").permitAll()
					.antMatchers("/swagger-ui/**").permitAll()
			    	.antMatchers("/swagger-resources/**").permitAll()
			    	.antMatchers("/v3/api-docs/**").permitAll()
			    	.antMatchers("/error").permitAll()
			    	.antMatchers("/favicon.ico").permitAll()
			    	.antMatchers("/sse/v1/alert/receive").permitAll()
			    	.antMatchers("/ws/**").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> {
				try {
					oauth2
							.loginPage("/oauth2/authorization/" + clientId + "-oidc")
					        .authorizationEndpoint()
					        .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
					        .and()
					        .redirectionEndpoint()
					        .baseUri("/login/oauth2/code/**")
					        .and()
					        .successHandler(oAuth2AuthenticationSuccessHandler)
					        .failureHandler(oAuth2AuthenticationFailureHandler)
					        .and()
					        .oauth2Client(withDefaults());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			)
			.addFilterAfter(getJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
			
        return http.build();
    }
    
    @Bean
    HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
	 * JWT 검증을 위한 필터 생성하여 반환.
	 * @return
	 */
	@Bean
	public JwtAuthenticationFilter getJwtAuthenticationFilter() {
		String[] authTokens = {apiToken, mlToken};		
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(publicKey, clientId, authTokens);
		return filter;
	}
}
