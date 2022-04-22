package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;

@Service
@Transactional//(rollbackFor = Exception.class)
public class ProjectUserDomainService {

	@Autowired
	ProjectUserRepository projectUserRepository;
	
	/**
     * Project User 리스트 조회(user_id)
     * @param userId
     * @return
     */
    /*public List<ProjectUserDto> getProjectByUserId(String userId) {
    	
    	return projectUserRepository.getProjectByUserId(userId);
    }*/
	
	/**
     * Project의 User 리스트 조회(Project Manager 제외)
     * @param userId
     * @return
     */
    public List<ProjectUserDto> getProjectUserListExceptManager(Long projectIdx) {
    	
    	return projectUserRepository.getProjectUserListExceptManager(projectIdx);
    }
	
	/**
	 * 프로젝트에 추가 할 수 있는 사용자 리스트 반환.
	 * @return
	 */
	public List<UserEntity> getAvailableProjectUserList() {
		return projectUserRepository.getAvailableProjectUserList();
	}
	
	/**
     * Project에서 사용중인 User를 제외한 리스트 조회
     * @param projectIdx
     * @return
     */
	public List<UserEntity> getProjectUserListExceptUse(Long projectIdx) {
		
		return projectUserRepository.getProjectUserListExceptUse(projectIdx);
	}
	
	/**
     * Project User 생성
     * @param entity
     * @return
     */
	public ProjectUserEntity createProjectUser(ProjectUserEntity entity) {
		
		return projectUserRepository.save(entity);
	}
	
	/**
     * Project User 삭제
     * @param projectIdx
     * @return
     */
	public Integer deleteProjectByProjectIdx(Long projectIdx) {
		
		return projectUserRepository.deleteByProjectIdx(projectIdx);
	}
	
	/**
     * Project User 삭제
     * @param projectIdx
     * @param userId
     * @return
     */
	public Integer deleteProjectUser(Long projectIdx, String userId) {
		
		return projectUserRepository.deleteByProjectIdxAndUserId(projectIdx, userId);
	}
	
	/**
     * Project의 User 조회
     * @param projectIdx
     * @param userId
     * @return
     */
	public ProjectUserEntity getProjectUser(Long projectIdx, String userId) {
		
		return projectUserRepository.findByProjectIdxAndUserId(projectIdx, userId);
	}
	
	/**
	 * 프로젝트 유저 권한 리턴.
	 * @param projectIdx
	 * @param userId
	 * @return
	 */
	public UserRoleEntity getProjectUserRole(Long projectIdx, String userId) {		
		return projectUserRepository.getProjectUserRole(projectIdx, userId);
	}
	
	/**
     * Project User 삭제(삭제요청한 User 삭제)
     * @param projectIdx
     * @return
     */
	public Integer deleteRequestProjectUser(Long projectIdx, List<String> userIds) {
		
		return projectUserRepository.deleteByProjectIdxAndUserIdNotIn(projectIdx, userIds);
	}
	
	/**
	 * 유저 아이디로 프로젝트 맵핑 정보 반환.
	 * @param userId
	 * @return
	 */
	public List<ProjectUserEntity> findByUserId(String userId) {
		return projectUserRepository.findByUserId(userId);
	}
	
	/**
	 * Project Manager 정보
	 * @param projectIdx
	 * @return
	 */
	public ProjectUserEntity getProjectManagerInfo(Long projectIdx) {
		return projectUserRepository.getProjectManagerInfo(projectIdx);
	}
	
	/**
	 * 사용자 권한이 Project Manager 인 사용자 조회
	 * @return
	 */
	public List<UserEntity> getUserWithManagerList() {
		return projectUserRepository.getUserWithManagerList();
	}
	
	/**
     * Project의 User 리스트 조회
     * @param userId
     * @return
     */
    public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
    	
    	return projectUserRepository.getProjectUserList(projectIdx);
    }
}
