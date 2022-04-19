package kr.co.strato.portal.setting.model;

import java.util.Date;

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
public class UserRoleMenuDto {	
	private Long id;
	private String createUserId;
	private String createUserName;
	private Date created_at;
	private String viewableYn;
	private String writableYn;
}
