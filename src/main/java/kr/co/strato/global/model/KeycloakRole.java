package kr.co.strato.global.model;

import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakRole {

	private String id;
	private String name;
	private String description;
	private boolean composite;
	private boolean clientRole;
	private String containerId;
	
}
