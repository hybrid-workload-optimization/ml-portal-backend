package kr.co.strato.portal.setting.model;

import java.util.List;

import kr.co.strato.global.model.PageRequest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorityDto extends PageRequest {
	private Long userRoleIdx;
	private String userRoleName;
	
	private List<Menu> menuList;
	
	private Long targetUserRoleIdx;
//	
////	private List<User> userList;
//	
	@Getter
	@Setter
	class Menu {
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
}
