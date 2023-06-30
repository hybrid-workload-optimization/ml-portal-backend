package kr.co.strato.oauth.controller;

import java.util.Base64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenCallbackController {
	
	@GetMapping(value = "/token")
	public String token(@RequestParam String token) {
		String tokenInfo = new String(Base64.getDecoder().decode(token.getBytes()));
		
		System.out.println(token);
		return tokenInfo;
	}
	
	@GetMapping(value = "/test")
	public String token() {
		return "test";
	}
}
