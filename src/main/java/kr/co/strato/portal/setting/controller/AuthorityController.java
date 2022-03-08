package kr.co.strato.portal.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityViewDto;
import kr.co.strato.portal.setting.service.AuthorityService;

@RestController
@RequestMapping("/api/v1/setting-authority")
public class AuthorityController {
	
	@Autowired
	private AuthorityService authorityService;
	
	//권한 전체 조회
	@GetMapping("/authorities")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<AuthorityViewDto>> getAllListUserRole(){
		List<AuthorityViewDto> authorityList = authorityService.getAllListAuthorityToDto();
		return new ResponseWrapper<>(authorityList);
	}
	
	//권한 상세
	@GetMapping("/authorities/{authId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<AuthorityViewDto> getUserRole(@PathVariable(name = "authId") Long authId){
		AuthorityViewDto authority = authorityService.getAuthorityToDto(authId);
		return new ResponseWrapper<>(authority);
	}
	
	//권한 신규생성
	@PostMapping("/authorities")
	public ResponseWrapper<Long> postUserRole(@RequestBody AuthorityRequestDto.ReqRegistDto param) {
		Long idx = authorityService.postUserRole(param);
		return new ResponseWrapper<>(idx);
	}
	
	//권한 삭제
	@DeleteMapping("/authorities")
	public ResponseWrapper<Long> deleteUserRole(@RequestBody AuthorityRequestDto.ReqDeleteDto param) {
		Long idx = authorityService.deleteUserRole(param);
		return new ResponseWrapper<>(idx);
	}
	
	//권한 수정
	@PatchMapping("/authorities")
	public ResponseWrapper<Long> modifyUserRole(@RequestBody AuthorityRequestDto.ReqModifyDto param) {
		Long idx = authorityService.modifyUserRole(param);
		return new ResponseWrapper<>(idx);
	}
	
}
