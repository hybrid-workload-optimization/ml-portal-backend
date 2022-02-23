package kr.co.strato.portal.project.model;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectUserDto {
	
	private String userId;
	private Long projectIdx;
	private String createUserId;
	private String createUserName;
	private String createdAt;
	private String projectUserRole;
	
	private String userName;
	private String email;
	private String organization;
	private int addDayCount;
}
