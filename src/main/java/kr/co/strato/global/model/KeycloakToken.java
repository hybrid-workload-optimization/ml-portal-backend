package kr.co.strato.global.model;

import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeycloakToken {

	private String accessToken;
	private int expiresIn;
	private String refreshToken;
	private int refreshExpiresIn;
	private String tokenType;
	private String sessionState;
	private String scope;
	private int notBeforePolicy;
}
