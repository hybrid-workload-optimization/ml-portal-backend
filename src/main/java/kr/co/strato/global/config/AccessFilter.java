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
import lombok.extern.java.Log;

@Component
@Log
public class AccessFilter implements Filter{

	
	@Autowired
	TokenValidator tokenValidator;
	
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
        
        System.out.println("path : " + path);
		String acToken = request.getHeader("access_token");
/*		
		Enumeration<String> names = request.getHeaderNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getHeader(name);
			
			System.out.println("name : " + name + " / value : " + value);
		}
		
		System.out.println(path);
		
	*/	

//		if(!path.contains("access-manage") && !path.contains("swagger") && !path.contains("test") && !path.contains("/users/dupl/") && !(path.contains("/users") && "POST".equals(method))) {
		if(!checkPath(path)) {
			String token = null;
			try {
				token = tokenValidator.decrypt(acToken, timestamp, path, method);
			} catch (Exception e) {
				e.printStackTrace();
				request.getSession(false);
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
			if(token == null) {
				log.info("Token Is Null");
				request.getSession(false);
				response.sendError(HttpStatus.UNAUTHORIZED.value());
			}else {
				if(!tokenValidator.validateToken(token)) {
					log.info("Token Is Not Validate");
					request.getSession(false);
					response.sendError(HttpStatus.UNAUTHORIZED.value());
				}else {
					chain.doFilter(request, response);
				}
			}	
		}else {
			chain.doFilter(request, response);
		}
		
    }
	
    @Override
    public void destroy() {
    }
    
    private boolean checkPath(String path) {
    	boolean result = false;
    	String[] arrPath = new String[] {"access-manage","swagger","test", "/users/dupl" , "users", "api-docs"};
    	result = Arrays.stream(arrPath)
    						.anyMatch(s -> path.contains(s));
    	return result;
    	
    }

}
