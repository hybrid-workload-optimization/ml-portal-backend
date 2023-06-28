package kr.co.strato.oauth.model;

import lombok.Data;

@Data
public class LogoutRequest {
	private String refresh_token;
	private String redirectUrl;
}
