package kr.co.strato.portal.setting.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorityRequestDto {
	private Long userRoleIdx;
	private String userRoleName;
	
//	@JsonIgnore
//	private List<MenuDto> menuList;
//	@JsonIgnore
//	private Long targetUserRoleIdx;

}
