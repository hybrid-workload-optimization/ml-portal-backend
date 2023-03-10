package kr.co.strato.adapter.sso.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersReq {
	
	private String clientId;
	private String companyName;
	private String email;
	private Boolean enabled; 
	private String username;
	
}
