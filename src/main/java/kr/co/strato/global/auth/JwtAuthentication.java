package kr.co.strato.global.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class JwtAuthentication extends AbstractAuthenticationToken {	
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;

	public JwtAuthentication(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public String getName() {
		if (this.getPrincipal() instanceof JwtToken) {
			return ((JwtToken) this.getPrincipal()).getPayload().getPreferredUsername();
		}
		return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
	}

}
