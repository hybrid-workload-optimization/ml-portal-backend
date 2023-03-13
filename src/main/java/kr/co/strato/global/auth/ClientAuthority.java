package kr.co.strato.global.auth;

import org.springframework.security.core.GrantedAuthority;

public class ClientAuthority implements GrantedAuthority {
	private static final long serialVersionUID = 100001L;
	
	
	private String authority;
	

	public ClientAuthority(String authority) {
		this.authority = authority;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}

}
