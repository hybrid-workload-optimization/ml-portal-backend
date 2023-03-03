package kr.co.strato.adapter.sso.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

	private String uuid;
	private String groupName;
	private String description;
	private String companyName;
	private List<String> allowClients;
	private String manager;
	private String createdAt;
	private String createdBy;
	private String updatedAt;
	private String updatedBy;
	
}
