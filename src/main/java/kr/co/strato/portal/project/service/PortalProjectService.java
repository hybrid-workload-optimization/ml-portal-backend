package kr.co.strato.portal.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.project.service.ProjectUserDomainService;
import kr.co.strato.portal.project.model.ProjectDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PortalProjectService {

	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectUserDomainService projectUserDomainService;
	
	/**
     * Project 리스트 조회
     * @param pageable
     * @param param
     * @return
     */
    public Page<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) {
    	
    	List<ProjectDto> projectList = projectDomainService.getProjectList(pageable, param);
    	
    	//페이징 정보 추가
        return new PageImpl<ProjectDto>(projectList, pageable, projectList.size());
    }
    
    /**
     * Project 상세 조회
     * @param projectId
     * @return
     */
    public ProjectDto getProjectDetail(Long projectId) {
    	
        return projectDomainService.getProjectDetail(projectId);
    }
}
