package kr.co.strato.portal.setting.model;

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
}
