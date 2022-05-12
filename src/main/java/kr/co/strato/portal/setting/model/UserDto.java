package kr.co.strato.portal.setting.model;

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
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class ChangeUserDto {
		private String userId;
		private String userPassword;
		private String userName;
		private String organization;
		private String contact;
	}
	
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class EnableUserDto {
		private String userId;
		private boolean enable;
	}
	
	
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
	
	
	
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class SearchParam{
        private Long projectId;
        private String authorityId;
        private String notAuthorityId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ResetRequestResult {
        private String result;
        private String userId;
        private String reason;
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
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UserMenuDto {
        private String userId;
        private String menuName;
        private Long userRoleMenuIdx;
        private String viewableYn;
        private String writableYn;
    }
}
