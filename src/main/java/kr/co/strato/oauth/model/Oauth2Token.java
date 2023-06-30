package kr.co.strato.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class Oauth2Token {
	
	@Data
	public static class RefreshTokenRequest {
		private String refresh_token;
	}	
	
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TokenResponse {
		private String access_token;
		private String refresh_token;
		private String token_type;
		private Long expires_in;
		private Long expires_at;
		private Long refresh_expires_in;
		private Long refresh_expires_at;
	}
	
}
