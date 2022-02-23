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
     * @return
     */
    public List<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) {
    	
    	//Project 리스트 조회
    	//return projectRepository.findAll(pageable);
    	return projectRepository.getProjectList(pageable, param);
    }
}
