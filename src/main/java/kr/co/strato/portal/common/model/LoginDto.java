package kr.co.strato.portal.common.model;

import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
	
	private UserDto user;
	private KeycloakToken token;
	
	
	@Override
	public String toString() {
		return "USER : " + user.toString() 
			+ " / token : " + token.toString();
	}

}
