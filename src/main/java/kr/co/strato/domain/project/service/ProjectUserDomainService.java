package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
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
     * Project의 User 리스트 조회
     * @param userId
     * @return
     */
    public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
    	
    	return projectUserRepository.getProjectUserList(projectIdx);
    }
}
