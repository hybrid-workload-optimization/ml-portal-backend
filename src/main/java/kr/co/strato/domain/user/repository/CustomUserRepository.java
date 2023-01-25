package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.portal.setting.model.UserDto;

public interface CustomUserRepository {
	
	Page<UserEntity> getListUserWithParam(Pageable pageable, UserDto.SearchParam param, UserDto loginUser);
	
	List<UserDto.UserMenuDto> getUserMenu(String userId);
}
