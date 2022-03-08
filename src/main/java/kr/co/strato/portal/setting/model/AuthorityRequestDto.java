package kr.co.strato.portal.setting.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorityRequestDto {
	
	@Getter
	@Setter
	@ToString
	public static class ReqViewDto {
		private Long userRoleIdx;
		private String userRoleName;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqRegistDto {
		private Long userRoleIdx;
		private String userRoleCode;
		private String userRoleName;
		private String description;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqDeleteDto {
		private Long userRoleIdx;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqModifyDto {
		private Long userRoleIdx;
		private String userRoleName;
		private List<Menu> menuList;
		private List<User> userList;
	}
	
	@Getter
	@Setter
	@ToString
	public static class Menu {
		private Long menuIdx;
		private String menuName;
		private String menuUrl;
		private Long parentMenuIdx;
		private Integer menuOrder;
		private Integer menuDepth;
		private String useYn;
		
		private String viewableYn;
		private String writableYn;
	}
	
	@Getter
	@Setter
	@ToString
	public static class User {
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
		private String useYn;
		
		private String type; //권한페이지에서 해당 사용자가 권한에 신규 매핑사용자인지, 삭제될(권한 초기화) 될 사용자인지 담을 용도 N(신규), D(변경)
	}
}
