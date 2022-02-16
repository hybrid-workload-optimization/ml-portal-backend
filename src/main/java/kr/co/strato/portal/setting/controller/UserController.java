package kr.co.strato.portal.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseWrapper<Long> postUser(){
		
		return new ResponseWrapper<>(0L);
	}
	
	//수정
	@PatchMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Long> patchUser(){
		
		return new ResponseWrapper<>(0L);
	}
	
	//삭제
	@DeleteMapping("/users/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Long> deleteUser(){
		
		return new ResponseWrapper<>(0L);
	}
	
	//목록
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<UserDto>> getUserList(){
		
		return null;
	}
	
	//상세
	@GetMapping("/users/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<UserDto>> getUserDetail(){
		
		return null;
	}
	

}
