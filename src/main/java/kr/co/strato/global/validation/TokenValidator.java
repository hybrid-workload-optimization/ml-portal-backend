package kr.co.strato.global.validation;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@Component
public class TokenValidator {

	@Value("${service.keycloak.public.key}")
	private String publicKey;
	
	public Map<String, Object> validateToken(String token) {
		Map<String, Object> result = null;
		try {
			Claims claims = Jwts.parser()
								.setSigningKey(publicKey.getBytes("UTF-8"))
								.parseClaimsJws(token)
								.getBody();
			
			
			result = claims;
			System.out.println(claims.toString());
			System.out.println(result.toString());
		}catch (ExpiredJwtException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}
