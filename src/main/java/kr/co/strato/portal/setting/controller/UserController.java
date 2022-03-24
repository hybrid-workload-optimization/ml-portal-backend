package kr.co.strato.portal.setting.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.service.UserService;

@RestController
@RequestMapping("/api/v1/user-manage")
public class UserController {
	
	@Autowired
	UserService userService;
	
	//등록
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<String> postUser(@RequestBody UserDto param, HttpSession session){
		String result = "N";
		//@TODO session 에서 로그인한 사용자 추가
		System.out.println("================ 등록 ===========");
		System.out.println(param.toString());
		System.out.println("================ 등록 ===========");
		
		// 등록 전 중복 체크
//			UserDto user = userService.getUserInfo(param.getUserId());
		
//		if(user != null) {
		if(true) {
			param.setUseYn("Y");
			userService.postUser(param);
			result = param.getUserId();
		}
		
		return new ResponseWrapper<>(result);
	}
	
	//수정
	@PatchMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchUser(@RequestBody UserDto param, HttpSession session){
		
		param.setUseYn("Y");
		userService.patchUser(param);
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//삭제
	@DeleteMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> deleteUser(@RequestParam(value = "userId") String userId, HttpSession session){
		
		UserDto param = new UserDto();
		param.setUserId(userId);
		userService.deleteUser(param);
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//목록
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<UserDto>> getUserList(PageRequest pageRequest, UserDto.SearchParam searchParam){
		
		if(pageRequest.getProperty() == null) pageRequest.setProperty("userId");
		
		Page<UserDto> list = null;
		try {
			list = userService.getAllUserList(pageRequest.of(), searchParam);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		return new ResponseWrapper<>(list);
	}
	
	//상세
	@GetMapping("/users/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserDto> getUserDetail(@PathVariable String userId){
		UserDto userDto = null;
		System.out.println("상세정보 user id : " + userId);
		try {
			userDto = userService.getUserInfo(userId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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
		
		System.out.println("==== 비밀번호 수정");
		
		userService.patchUserPassword(param);
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	// UserId 회원가입 여부 확인
	@GetMapping("/users/dupl/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> checkDuplUser(@PathVariable String userId){
		String result = "N";
		UserDto user = userService.getUserInfo(userId);
		
		if(user != null) {
			result ="Y";
		}
		
		return new ResponseWrapper<>(result);
		
	}
	
	@GetMapping("/test")
	@ResponseStatus(HttpStatus.OK)
	public void testController() {
		System.out.println("============>> 테스트");
		
		userService.getTest();
		
		System.out.println("<<============ 테스트 종료");
	}
	

}
