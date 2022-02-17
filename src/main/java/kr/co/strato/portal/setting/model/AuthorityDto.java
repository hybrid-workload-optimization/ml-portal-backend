package kr.co.strato.portal.setting.model;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

@Data
public class AuthorityDto {
	private Long userRoleIdx;
	private String userRoleName;
}
