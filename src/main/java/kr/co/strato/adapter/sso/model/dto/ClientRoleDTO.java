package kr.co.strato.adapter.sso.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRoleDTO {
	
	private String uuid;
	private String roleName;
	private String description;
	
}
