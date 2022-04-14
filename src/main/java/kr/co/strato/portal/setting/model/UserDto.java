package kr.co.strato.portal.setting.model;

import javax.persistence.Column;
import javax.persistence.Id;

import kr.co.strato.domain.user.model.UserRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
	
	private String userId;
	private String userPassword;
	private String userName;
	private String email;
	private String organization;
	private String contact;
	private String updatedAt;
	private String updateUserId;
	private String updateUserName;
	private String createUserId;
	private String createUserName;
	private String createdAt;
	private String useYn;
	private UserRole userRole;
	
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class UserRole {
		private Long userRoleIdx;
		private String userRoleCode;
		private String userRoleName;
		private String description;
		private String groupYn;
		private Long parentUserRoleIdx;
	}
	
	public UserDto(String userId, String userName, String email, String organization, String useYn, UserRole userRole) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.organization = organization;
		this.useYn = useYn;
		this.userRole = userRole;
	}
	
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class SearchParam{
        private Long projectId;
        private String authorityId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ResetParam{
        private String requestCode;
        private String userId;
        private String userPassword;
    }
}
