package kr.co.strato.global.config;

import java.io.IOException;
import java.util.Enumeration;

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

import kr.co.strato.global.validation.TokenValidator;

@Component
public class AccessFilter implements Filter{

	
	@Autowired
	TokenValidator tokenValidator;
	
    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
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
        
        System.out.println(timestamp);
        
		String acToken = request.getHeader("access_token");
//		Enumeration<String> names = request.getHeaderNames();
//		while(names.hasMoreElements()) {
//			String name = names.nextElement();
//			String value = request.getHeader(name);
//			
//			System.out.println("name : " + name + " / value : " + value);
//					
//		}
		
//		try {
//			System.out.println(tokenValidator.encrypt(acToken, timestamp, path, method));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
		
/*
		if(!path.contains("access-manage")) {
			String token = null;
			try {
				token = tokenValidator.decrypt(acToken, timestamp, path, method);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(token);
			if(token == null) {
				System.out.println("token is null...");
				request.getSession(false);
				response.sendError(HttpStatus.UNAUTHORIZED.value());
			}else {
				if(!tokenValidator.validateToken(token)) {
					System.out.println("token is not validate");
					request.getSession(false);
					response.sendError(HttpStatus.UNAUTHORIZED.value());
				}
			}	
		}
		*/
		chain.doFilter(request, response);
    }
	
    @Override
    public void destroy() {
    }

}
