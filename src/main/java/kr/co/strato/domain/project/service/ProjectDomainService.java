package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.project.repository.ProjectRepository;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.project.model.ProjectClusterDto.ProjectClusterDtoBuilder;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectDto.ProjectDtoBuilder;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.model.ProjectUserDto.ProjectUserDtoBuilder;
import kr.co.strato.portal.project.model.mapper.ProjectClusterDtoMapper;
import kr.co.strato.portal.project.model.mapper.ProjectDtoMapper;
import kr.co.strato.portal.project.model.mapper.ProjectUserDtoMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectDomainService {

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
	@Autowired
	ProjectUserRepository projectUserRepository;
	
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
     * @param projectIdx
     * @return
     */
    public ProjectDto getProjectDetail(Long projectIdx) {
    	
    	return projectRepository.getProjectDetail(projectIdx);
    }
    
    /**
     * Project 생성
     * @param
     * @return
     */
    public Long createProject(ProjectEntity projectEntity) throws Exception {
    	
    	projectRepository.save(projectEntity);
    	
    	return projectEntity.getId();
    }
}
