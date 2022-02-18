package kr.co.strato.portal.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.AuthorityDto;
import kr.co.strato.portal.setting.service.AuthorityService;

@RestController
@RequestMapping("/api/v1/setting-authority")
public class AuthorityController {
	
	@Autowired
	private AuthorityService authorityService;
	
	//목록
	@GetMapping("/authorities")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<AuthorityDto>> getListUserRole(@RequestBody AuthorityDto param){
		Page<AuthorityDto> userList = authorityService.getListAuthorityDto(param, param.of());
		return new ResponseWrapper<>(userList);
	}
	
	//상세
	@GetMapping("/authorities/{authId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserRoleEntity> getUserRole(@PathVariable(name = "authId") Long authId, @RequestBody AuthorityDto param){
		return new ResponseWrapper<>(null);
	}
	
}
