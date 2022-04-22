package kr.co.strato.domain.project.repository;

import java.util.List;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;

public interface ProjectUserRepositoryCustom {

	//public List<ProjectUserDto> getProjectByUserId(String userId);
	
	public List<ProjectUserDto> getProjectUserListExceptManager(Long projectIdx);
	
	public List<UserEntity> getProjectUserListExceptUse(Long projectId);
	
	public List<UserEntity> getAvailableProjectUserList();
	
	/**
	 * 프로젝트 유저롤 반환.
	 * @param projectIdx
	 * @param userId
	 * @return
	 */
	public UserRoleEntity getProjectUserRole(Long projectIdx, String userId);
	
	public ProjectUserEntity getProjectManagerInfo(Long projectId);
	
	public List<UserEntity> getUserWithManagerList();
	
	public List<ProjectUserDto> getProjectUserList(Long projectIdx);
}