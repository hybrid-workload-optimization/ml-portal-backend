package kr.co.strato.portal.setting.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorityViewDto {

	private Long userRoleIdx;
	private String userRoleName;
	private Long targetUserRoleIdx;
	
	private List<Menu> menuList;	// 권한별 메뉴
	private List<User> userList;	// 권한별 사용자
	
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
	}
	
}

