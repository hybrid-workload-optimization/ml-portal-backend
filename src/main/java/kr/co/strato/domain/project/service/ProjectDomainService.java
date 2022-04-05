package kr.co.strato.domain.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.project.repository.ProjectRepository;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.portal.project.model.ProjectDto;

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
    public PageImpl<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) throws Exception {
    	
    	return projectRepository.getProjectList(pageable, param);
    }
    
    /**
     * Project 상세 조회
     * @param projectIdx
     * @return
     */
    public ProjectDto getProjectDetail(Long projectIdx, String type) {
    	
    	return projectRepository.getProjectDetail(projectIdx, type);
    }
    
    /**
     * Project 생성
     * @param
     * @return
     */
    public Long createProject(ProjectEntity projectEntity) {
    	
    	projectRepository.save(projectEntity);
    	
    	return projectEntity.getId();
    }

	public List<ProjectEntity> getProjects(){
		return projectRepository.findAll();
	}
	
	/**
     * Project 조회
     * @param
     * @return
     */
    public Optional<ProjectEntity> getProjectById(Long projectIdx) {
    	
    	return projectRepository.findById(projectIdx);
    }
	
	/**
     * Project 수정
     * @param
     * @return
     */
    public ProjectEntity updateProject(ProjectEntity projectEntity) {
    	
    	return projectRepository.save(projectEntity);
    }
    
    /**
     * Project 삭제
     * @param
     * @return
     */
    public void deleteProject(ProjectEntity projectEntity) {
    	
    	projectRepository.save(projectEntity);
    }
    
    /**
     * Project 조회
     * @param
     * @return
     */
    public Optional<ProjectEntity> getProjectByProjectName(String projectName, String deletedYn) {
    	
    	return projectRepository.findByProjectNameAndDeletedYn(projectName, deletedYn);
    }
    
    /**
     * ClusterId가 소속 된 Project 상세 조회
     * @param clusterIdx : ClusterId
     * @return ProjectDto 반환, 소속된 Project가 없는 경우 null 반환
     */
    public ProjectEntity getProjectDetailByClusterId(Long clusterIdx) {
    	
    	return projectRepository.getProjectDetailByClusterId(clusterIdx);
    }
}
