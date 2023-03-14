package kr.co.strato.adapter.sso.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailDTO {

	private String uuid;
	private String groupName;
	private String description;
	private String companyName;
	private List<String> allowClients;
	private UserDTO manager;
	private String managerId;
	private String createdAt;
	private String createdBy;
	private String updatedAt;
	private String updatedBy;
	private List<UserDTO> members;
	private Object clientRoles;
	
}