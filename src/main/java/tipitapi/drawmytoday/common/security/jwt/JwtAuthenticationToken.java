package tipitapi.drawmytoday.common.security.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;
	private final String credentials;

	public JwtAuthenticationToken(Object principal, String credentials) {
		super(null);
		super.setAuthenticated(false);
		this.principal = principal;
		this.credentials = credentials;
	}

	public JwtAuthenticationToken(Object principal, String credentials,
		Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		super.setAuthenticated(true);
		this.principal = principal;
		this.credentials = credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public String getCredentials() {
		return credentials;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		if (authenticated) {
			throw new IllegalArgumentException("can't change authenticate because authenticate already true");
		}
		super.setAuthenticated(false);
	}
}
