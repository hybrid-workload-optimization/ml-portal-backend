package kr.co.strato.global.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RequestAuthenticator;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.authentication.RequestAuthenticatorFactory;
import org.keycloak.adapters.springsecurity.authentication.SpringSecurityRequestAuthenticatorFactory;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.token.AdapterTokenStoreFactory;
import org.keycloak.adapters.springsecurity.token.SpringSecurityAdapterTokenStoreFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.Assert;

public class KeycloakAuthenticationProcessingFilterEx extends KeycloakAuthenticationProcessingFilter {	
	private ApplicationContext applicationContext;
	private AdapterDeploymentContext adapterDeploymentContext;
	private AdapterTokenStoreFactory adapterTokenStoreFactory = new SpringSecurityAdapterTokenStoreFactory();
	private RequestAuthenticatorFactory requestAuthenticatorFactory = new SpringSecurityRequestAuthenticatorFactory();

	
	private final List<String> allowUrls = Arrays.asList("/login", "/sso/login", "/error", "/favicon.ico");
	
	public KeycloakAuthenticationProcessingFilterEx(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	/**
	 * 인증이 없는 상태에서 로그인 요청 이외의 API call은 401 에러를 발생 시킨다.
	 * 원래는 인증 없은 상태에서 /sso/login 페이지로 넘어갔음.
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void  doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String requestURI = request.getRequestURI();		
		
		if(!allowUrls.contains(requestURI)) {			
			//인증 없음이 허용되지 않은 URL
			HttpFacade facade = new SimpleHttpFacade(request, response);
	        KeycloakDeployment deployment = adapterDeploymentContext.resolveDeployment(facade);

	        deployment.setDelegateBearerErrorResponseSending(true);

	        AdapterTokenStore tokenStore = adapterTokenStoreFactory.createAdapterTokenStore(deployment, request, response);
	        RequestAuthenticator authenticator
	                = requestAuthenticatorFactory.createRequestAuthenticator(facade, request, deployment, tokenStore, -1);

	        AuthOutcome result = authenticator.authenticate();
	        if (AuthOutcome.NOT_ATTEMPTED.equals(result) || AuthOutcome.FAILED.equals(result)) {
	        	
	        	//로그인 권한이 없는 경우 401 에러 발생 후 리턴.
	    		request.getSession(false);
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
	    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
	    		return;
	        }
		}
		
		super.doFilter(request, response, chain);
	}	
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        adapterDeploymentContext = applicationContext.getBean(AdapterDeploymentContext.class);
        super.setApplicationContext(applicationContext);
    }
	
	@Override
    public void afterPropertiesSet() {
        adapterDeploymentContext = applicationContext.getBean(AdapterDeploymentContext.class);
        super.afterPropertiesSet();
    }
	
	@Override
	public void setAdapterTokenStoreFactory(AdapterTokenStoreFactory adapterTokenStoreFactory) {
        Assert.notNull(adapterTokenStoreFactory, "AdapterTokenStoreFactory cannot be null");
        this.adapterTokenStoreFactory = adapterTokenStoreFactory;
        super.setAdapterTokenStoreFactory(adapterTokenStoreFactory);
    }
	
	@Override
	public void setRequestAuthenticatorFactory(RequestAuthenticatorFactory requestAuthenticatorFactory) {
        Assert.notNull(requestAuthenticatorFactory, "RequestAuthenticatorFactory cannot be null");
        this.requestAuthenticatorFactory = requestAuthenticatorFactory;
        super.setRequestAuthenticatorFactory(requestAuthenticatorFactory);
    }
}
