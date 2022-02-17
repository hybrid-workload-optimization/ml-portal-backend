package kr.co.strato.portal.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.service.UserRoleService;

@RestController
@RequestMapping("/api/v1/user-role")
public class UserRoleController {
	
	@Autowired
	private UserRoleService portalUserRoleService;
	
	//목록
	@GetMapping("/roles")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<UserRoleDto>> getListUserRole(){
		
		List<UserRoleDto> userList = portalUserRoleService.getListUserRoleDto();
		
		return new ResponseWrapper<>(userList);
	}
	
	//상세
	@GetMapping("/roles/{roleId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<UserRoleEntity> getUserRole(@PathVariable(name = "roleId") Long roleId, @RequestBody UserRoleDto param){
		System.out.println("####roleId :: " + roleId);
		System.out.println("####param :: " + param.toString());
		return new ResponseWrapper<>(null);
	}
	
}
