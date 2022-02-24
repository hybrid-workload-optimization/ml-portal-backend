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
    public Long createProject(ProjectRequestDto param) throws Exception {
    	
    	String userId = param.getLoginId();
    	String userName = param.getLoginName();
    	String now = DateUtil.currentDateTime("yyyy-MM-dd hh:mm:ss");
    	
    	ProjectDtoBuilder projectBuiler = ProjectDto.builder();
    	projectBuiler.projectName(param.getProjectName());
    	projectBuiler.description(param.getDescription());
    	projectBuiler.createUserId(userId);
    	projectBuiler.createUserName(userName);
    	projectBuiler.createdAt(now);
    	projectBuiler.updateUserId(userId);
    	projectBuiler.updateUserName(userName);
    	projectBuiler.updatedAt(now);
    	projectBuiler.deletedYn("N");
    	
    	//ProjectDTO -> ProjectEntity
        ProjectEntity projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
    	projectRepository.save(projectEntity);
    	
    	Long resultIdx = projectEntity.getId();
    	
    	if(resultIdx == null) {
    		throw new Exception();
    	} else {
    		
    		//Project Cluster 등록
    		List<ProjectClusterDto> clusterList = param.getClusterList();
        	for(ProjectClusterDto cluster : clusterList) {
        		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
        		projectClusterBuiler.projectIdx(resultIdx);
        		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
        		
        		//ProjectClusterDTO -> ProjectClusterEntity
                ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                projectClusterRepository.save(projectClusterEntity);
        	}
    		
    		//Project User 등록
    		List<ProjectUserDto> userList = param.getUserList();
        	for(ProjectUserDto user : userList) {
        		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
        		projectUserBuiler.userId(user.getUserId());
        		projectUserBuiler.projectIdx(resultIdx);
        		projectUserBuiler.createUserId(userId);
        		projectUserBuiler.createUserName(userName);
        		projectUserBuiler.createdAt(now);
        		projectUserBuiler.projectUserRole(user.getProjectUserRole());
        		
        		//ProjectUserDTO -> ProjectUserEntity
                ProjectUserEntity projectUserEntity = ProjectUserDtoMapper.INSTANCE.toEntity(projectUserBuiler.build());
                projectUserRepository.save(projectUserEntity);
        	}
    	}
    	
    	return resultIdx;
    }
}
