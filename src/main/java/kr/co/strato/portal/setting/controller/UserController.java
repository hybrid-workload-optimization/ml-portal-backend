package kr.co.strato.portal.setting.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
		//@TODO session 에서 로그인한 사용자 추가
		System.out.println("================ 등록 ===========");
		System.out.println(param.toString());
		System.out.println("================ 등록 ===========");
		param.setUseYn("Y");
		userService.postUser(param);
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//수정
	@PatchMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchUser(@RequestBody UserDto param, HttpSession session){
		
		System.out.println("================ 수정 ===========");
		System.out.println(param.toString());
		System.out.println("================ 수정 ===========");
	
		param.setUseYn("Y");;
		userService.patchUser(param);
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//삭제
	@DeleteMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> deleteUser(@RequestParam(value = "userId") String userId, HttpSession session){
		
		System.out.println("================ 삭제 ===========");
		System.out.println(userId);
		System.out.println("================ 삭제 ===========");
		UserDto param = new UserDto();
		param.setUserId(userId);
		userService.deleteUser(param);
		
		return new ResponseWrapper<>(param.getUserId());
	}
	
	//목록
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<UserDto>> getUserList(PageRequest pageRequest){
		
		if(pageRequest.getProperty() == null) pageRequest.setProperty("userId");
		
		Page<UserDto> list = null;
		try {
			list = userService.getAllUserList(pageRequest.of());
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		System.out.println("=====전체 목록=====");
		if(list != null) System.out.println(list.toString());
		
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
		System.out.println("============= 유저 상세정보");
		System.out.println(userDto);
		
		return new ResponseWrapper<>(userDto);
	}
	

}
