package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.domain.project.repository.ProjectRepository;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectDomainService {

	@Autowired
	ProjectRepository projectRepository;
	
	/**
     * Project 리스트 조회
     * @param pageable
     * @param param
     * @return
     */
    public List<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) {
    	
    	return projectRepository.getProjectList(pageable, param);
    }
    
    /**
     * Project 상세 조회
     * @param projectId
     * @return
     */
    public ProjectDto getProjectDetail(Long projectId) {
    	
    	return projectRepository.getProjectDetail(projectId);
    }
}
