package kr.co.strato.global.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import kr.co.strato.global.error.exception.AuthFailException;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccessFilter implements Filter{

	
	@Autowired
	TokenValidator tokenValidator;
	
	@Autowired
	AccessService accessService;
	
    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException, AuthFailException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        
        String path = request.getServletPath();
        if(path.contains("?")) {
        	path = path.split("?")[0];
        }
        String method = request.getMethod();
        String strTimestamp = request.getHeader("timestamp");
        Long timestamp = 0L;
        if(strTimestamp != null) {
        	timestamp = Long.parseLong(strTimestamp);	
        }

		String acToken = request.getHeader("access-token");
		
		if(!checkPath(path, method)) {
			if(acToken != null) {
				String token = null;
				try {
					token = tokenValidator.decrypt(acToken, timestamp, path, method);
				} catch (Exception e) {
					e.printStackTrace();
					request.getSession(false);
					response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
				}
				if(token == null) {
					log.info("Token is null");
					request.getSession(false);
					response.sendError(HttpStatus.UNAUTHORIZED.value());
				} else {
					if(!tokenValidator.validateToken(token)) {
						log.info("Token is not validate. Token: {}", token);
						
						//토큰 기간 만료
						request.getSession(false);
						response.sendError(HttpStatus.UNAUTHORIZED.value());
					} else {
						UserDto loginUser = tokenValidator.extractUserInfo(token);
						request.setAttribute("loginUser", loginUser);
						chain.doFilter(request, response);
					}
				}	
			} else {
				log.error("Token is null");
				request.getSession(false);
				response.sendError(HttpStatus.UNAUTHORIZED.value());
			}
		} else {			
			chain.doFilter(request, response);
		}
    }
	
    @Override
    public void destroy() {
    }
    
    private boolean checkPath(String path, String method) {
    	boolean result = false;
    	String[] arrPath = new String[] {
    			"access-manage",
    			"swagger", 
    			"test", 
    			"/users/dupl" , 
    			"api-docs", 
    			"/ws", 
    			"/api/v1/work-job/callback", 
    			"favicon.ico",
    			"/users/reset/password"};
    	
    	if("POST".equals(method) && path.contains("users")) {
    		// 회원가입 액션
    		return true;
    	}
    	
    	result = Arrays.stream(arrPath)
    						.anyMatch(s -> path.contains(s));
    	
    	return result;
    }
    

}
