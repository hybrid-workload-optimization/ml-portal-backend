package kr.co.strato.portal.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.project.service.ProjectUserDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.model.mapper.ProjectDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PortalProjectService {

	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectUserDomainService projectUserDomainService;
	
	@Autowired
	ProjectClusterDomainService projectClusterDomainService;
	
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
     * @param projectIdx
     * @return
     */
    public ProjectDto getProjectDetail(Long projectIdx) {
    	
        return projectDomainService.getProjectDetail(projectIdx);
    }
    
    /**
     * Project의 Cluster 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<ProjectClusterDto> getProjectClusterList(Long projectIdx) {
    	
        return projectClusterDomainService.getProjectClusterList(projectIdx);
    }
    
    /**
     * Project의 User 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
    	
        return projectUserDomainService.getProjectUserList(projectIdx);
    }
    
    /**
     * Project 생성
     * @param
     * @return
     */
    public Long createProject(ProjectRequestDto param) throws Exception {
    	
    	return projectDomainService.createProject(param);
    }
}
