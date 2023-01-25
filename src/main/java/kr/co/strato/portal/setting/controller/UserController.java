package kr.co.strato.portal.setting.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserDto.EnableUserDto;
import kr.co.strato.portal.setting.service.UserService;
import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.work.service.WorkHistoryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user-manage")
@Slf4j
public class UserController extends CommonController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	TokenValidator tokenValidator;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	//등록
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<String> postUser(@RequestBody UserDto param, HttpSession session){
		String result = "N";
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> meta = mapper.convertValue(param, Map.class);
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= meta;
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        
        UserDto loginUser = getLoginUser();
		
        
		param.setUseYn("Y");
		try {
			userService.postUser(param, loginUser);
			result = param.getUserId();
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.INSERT)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		return new ResponseWrapper<>(result);
	}
	
	//수정
	@PatchMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchUser(@RequestBody UserDto param, HttpSession session){
		
		String workTarget					= null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> meta = mapper.convertValue(param, Map.class);
        Map<String, Object> workMetadata	= meta;
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";

		try {
			param.setUseYn("Y");
			userService.patchUser(param);
			
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.UPDATE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//삭제
	@DeleteMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> deleteUser(@RequestParam(value = "userId") String userId, HttpSession session){
		
		UserDto param = new UserDto();
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        workMetadata.put("userId", userId);
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";

		try {
			param.setUserId(userId);
			userService.deleteUser(param);
			
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.DELETE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//유저 활성화/비활성화
	@PutMapping("/user/enable")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserDto> enableUser(@RequestBody EnableUserDto param){
		String workTarget = null;
        Map<String, Object> workMetadata = new HashMap<>();
        workMetadata.put("userId", param.getUserId());
        WorkResult workResult = WorkResult.SUCCESS;
        String workMessage = "";
        
        UserDto user = null;

		try {
			user = userService.enableUser(param, getLoginUser());
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.UPDATE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		return new ResponseWrapper<>(user);
	}
	
	//목록
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<UserDto>> getUserList(PageRequest pageRequest, UserDto.SearchParam searchParam, HttpServletRequest request){
		
		if(pageRequest.getProperty() == null) pageRequest.setProperty("userId");
		
//		tokenValidator.extractUserInfo(request.getHeader("access_token"));
		
		Page<UserDto> list = null;
		
		String workTarget					= null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> meta = mapper.convertValue(searchParam, Map.class);
        Map<String, Object> workMetadata	= meta;
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";

		try {
			list = userService.getAllUserList(pageRequest.of(), searchParam, getLoginUser());
			
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.LIST)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		return new ResponseWrapper<>(list);
	}
	
	//상세
	@GetMapping("/users/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserDto> getUserDetail(@PathVariable String userId){
		UserDto userDto = null;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
//        workMetadata.put("userId", decodeUrl(userId));
        workMetadata.put("userId", userId);
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";

		try {
//			userDto = userService.getUserInfo(decodeUrl(userId));
			userDto = userService.getUserInfo(userId);
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.DETAIL)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		return new ResponseWrapper<>(userDto);
	}
	
	//유저 Role List
	@GetMapping("/users/roles")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<UserRoleDto>> getUserRoleList(){
		List<UserRoleDto> list = userService.getUserRoleList();
		return new ResponseWrapper<>(list);
	}
		
	// 비밀번호 변경
	@PatchMapping("/users/password")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchUserPassword(@RequestBody UserDto param, HttpSession session){
		
		String workTarget					= null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> meta = mapper.convertValue(param, Map.class);
        Map<String, Object> workMetadata	= meta;
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "비밀번호 변경";
		
		param.setUseYn("Y");
		try {
			userService.patchUserPassword(param.getUserId(), param.getUserPassword());
		}catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.UPDATE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	// UserId 회원가입 여부 확인
	@GetMapping("/users/dupl/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> checkDuplUser(@PathVariable String userId){
		String result = "Y"; // Y : 없음 , N : 있음
		try {
//			UserDto user = userService.getUserInfo(decodeUrl(userId));
			UserDto user = userService.getUserInfo(userId);
			
			if(user != null) { // 유저 객체가 존재하면 result : N
				result ="N";
			}
		}catch (NotFoundResourceException e) {
			result = "Y";
		}catch (Exception e) {
			result = "N";
			log.error(e.getMessage(), e);
		}
		
		return new ResponseWrapper<>(result);
	}
	
	private String decodeUrl(String msg) throws UnsupportedEncodingException {
		String result = URLDecoder.decode(msg, StandardCharsets.UTF_8.toString()).replaceAll("%2E",".");
		
		return result;
	}
	
	
	/**
	 * 패스워드 변경 페이지 요청.
	 * @param requestCode
	 * @return
	 
	@GetMapping("/users/reset/password/page")
	@ResponseStatus(HttpStatus.OK)
	public void resetPasswordPage(HttpServletResponse response, 
			@RequestParam String requestCode) throws IOException {
		String url = userService.getResetPasswordUrl(requestCode);
		response.sendRedirect(url);
	}
	*/
	
	//수정
	@PutMapping("/user")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserDto> patchUser(@RequestBody UserDto.ChangeUserDto changeUserDto){
		String workTarget					= null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> meta = mapper.convertValue(changeUserDto, Map.class);
        Map<String, Object> workMetadata	= meta;
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";

        UserDto userDto = null;
		try {
			userDto = userService.updateUser(changeUserDto, getLoginUser());
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.SETTING)
						.workMenu2(WorkMenu2.USER)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.UPDATE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
		return new ResponseWrapper<>(userDto);
	}
	
	//수정
	@GetMapping("/user/menu")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<UserDto.UserMenuDto>> userMenu(){
		
        List<UserDto.UserMenuDto> userMenuDto = null;
		try {
			userMenuDto = userService.getUserMenu(getLoginUser().getUserId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
		
		return new ResponseWrapper<>(userMenuDto);
	}
	
}
