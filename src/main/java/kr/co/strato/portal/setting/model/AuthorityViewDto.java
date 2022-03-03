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
	
	private List<MenuDto> menuList;
	private Long targetUserRoleIdx;
}
