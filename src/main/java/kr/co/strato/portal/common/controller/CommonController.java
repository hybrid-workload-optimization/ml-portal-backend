package kr.co.strato.portal.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.co.strato.portal.setting.model.UserDto;

@RestController
public class CommonController {
	
	/**
	 * 로그인한 유저 정보 반환.
	 * @return
	 */
	public UserDto getLoginUser() {
		HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    	Object obj = servletRequest.getAttribute("loginUser");
    	if(obj != null) {
    		return (UserDto) obj;
    	}
    	return null;
	}
}
