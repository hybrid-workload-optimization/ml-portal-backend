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
public class UserRoleDto {

	private Long userRoleIdx;
	private String userRoleCode;
	private String userRoleName;
	private String description;
	private String groupYn;
	private Long parentUserRoleIdx;
}
