package kr.co.strato.portal.setting.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserAuthorityDto {
	
	//유저 아이디
	private String userId;
	
	//디폴트 유저 롤
	private List<AuthorityViewDto.Menu> defaultUserRole;
	
	//프로젝트 별 유저 롤
	private Map<Long, List<AuthorityViewDto.Menu>> projectUserRole;
	
}
