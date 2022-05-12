package kr.co.strato.portal.project.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectRequestDto {

	private Long projectIdx;
	
	private String loginId;
	private String loginName;
	
	private String projectName;
	private ProjectUserDto projectManager;
	private String description;
	
	private List<ProjectClusterDto> clusterList;
	private List<ProjectUserDto> userList;
}
