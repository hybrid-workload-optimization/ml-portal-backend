package kr.co.strato.portal.setting.model;

import javax.persistence.Column;
import javax.persistence.Id;

import kr.co.strato.domain.user.model.UserRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class UserDto {
	
	private String userId;
	
	private String password;
	
	private String userName;
	
	private String email;
	
	private String organization;
	
	private String contact;
	
	private String updateUserId;
	
	private String updateUserName;
	
	private String createUserId;
	
	private String createUserName;
	
	private String createdAt;

	private Long userRoleIdx;
	
	private String useYn;
	
	public UserDto() {
		
	}
	
	public UserDto(String userId, String userName, String email, String organization, String useYn) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.organization = organization;
		this.useYn = userId;
	}
}
