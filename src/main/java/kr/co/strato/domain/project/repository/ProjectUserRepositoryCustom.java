package kr.co.strato.domain.project.repository;

import java.util.List;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;

public interface ProjectUserRepositoryCustom {

	//public List<ProjectUserDto> getProjectByUserId(String userId);
	
	public List<ProjectUserDto> getProjectUserList(Long projectIdx);
	
	public List<UserEntity> getProjectUserListExceptUse(Long projectId);
	
	public List<UserEntity> getAvailableProjectUserList();
}