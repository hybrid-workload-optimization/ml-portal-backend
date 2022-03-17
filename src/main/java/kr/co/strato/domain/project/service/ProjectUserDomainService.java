package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.portal.project.model.ProjectUserDto;

@Service
@Transactional//(rollbackFor = Exception.class)
public class ProjectUserDomainService {

	@Autowired
	ProjectUserRepository projectUserRepository;
	
	@Autowired
	UserRepository userRepository;
	
	/**
     * Project User 리스트 조회(user_id)
     * @param userId
     * @return
     */
    /*public List<ProjectUserDto> getProjectByUserId(String userId) {
    	
    	return projectUserRepository.getProjectByUserId(userId);
    }*/
	
	/**
     * Project의 User 리스트 조회
     * @param userId
     * @return
     */
    public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
    	
    	return projectUserRepository.getProjectUserList(projectIdx);
    }
    
    /**
     * 현재 사용중인 User 리스트 조회
     * @param useYn
     * @return
     */
	public List<UserEntity> getProjecUserListByUseYn(String useYn) {
		
		return userRepository.findByUseYn(useYn);
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
}
