package kr.co.strato.global.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakUser {
	private String id;
	private long createdTimestamp;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private boolean enabled;
	private boolean emailVerified;
	private KeycloakAccess access;
	
	
}
