package kr.co.strato.portal.project.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.project.service.ProjectUserDomainService;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.global.error.exception.AleadyUserClusterException;
import kr.co.strato.global.error.exception.CreateProjectFailException;
import kr.co.strato.global.error.exception.DeleteProjectFailException;
import kr.co.strato.global.error.exception.NotFoundProjectException;
import kr.co.strato.global.error.exception.UpdateProjectFailException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
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
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDtoMapper;
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
     * 로그인한 사용자가 생성한 Cluster 리스트 조회
     * @param loginId
     * @return
     */
    public List<ClusterDto> getProjecClusterListByCreateUserId(String loginId) {
    	
    	List<ClusterEntity> clusterList = projectClusterDomainService.getProjecClusterListByCreateUserId(loginId);
    	
    	//Entity -> DTO 변환
    	return clusterList.stream().map(m -> ClusterDtoMapper.INSTANCE.toDto(m)).collect(Collectors.toList());
    }
    
    /**
     * 현재 사용중인 전체 User 리스트 조회
     * @param useYn
     * @return
     */
    public List<UserDto> getProjecUserListByUseYn(String useYn) {
    	
    	List<UserEntity> userList = projectUserDomainService.getProjecUserListByUseYn(useYn);
    	
    	//Entity -> DTO 변환
    	return userList.stream().map(m -> UserDtoMapper.INSTANCE.toDto(m)).collect(Collectors.toList());
    }
    
    /**
     * Project 생성
     * @param
     * @return
     */
    public Long createProject(ProjectRequestDto param) throws CreateProjectFailException {
    	
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
    	
    	Long resultIdx = null;
    	
    	try {
    		//ProjectDTO -> ProjectEntity
            ProjectEntity projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
            resultIdx = projectDomainService.createProject(projectEntity);
        	
            if(resultIdx == null) {
            	throw new CreateProjectFailException();
            } else {
            	//Project Cluster 등록
        		List<ProjectClusterDto> clusterList = param.getClusterList();
            	for(ProjectClusterDto cluster : clusterList) {
            		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
            		projectClusterBuiler.projectIdx(resultIdx);
            		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
            		
            		//ProjectClusterDTO -> ProjectClusterEntity
                    ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                    projectClusterDomainService.createProjectCluster(projectClusterEntity);
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
                    projectUserDomainService.createProjectUser(projectUserEntity);
            	}
            }
    	} catch(Exception e) {
    		throw new CreateProjectFailException();
    	}
    	
    	return resultIdx;
    }
    
    /**
     * Project에서 사용중인 Cluster를 제외한 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<ClusterDto> getProjectClusterListExceptUse(Long projectIdx) {
    	
    	List<ClusterEntity> clusterList = projectClusterDomainService.getProjectClusterListExceptUse(projectIdx);
    	
    	//Entity -> DTO 변환
    	return clusterList.stream().map(m -> ClusterDtoMapper.INSTANCE.toDto(m)).collect(Collectors.toList());
    }
    
    /**
     * Project에서 사용중인 User를 제외한 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<UserDto> getProjectUserListExceptUse(Long projectIdx) {
    	
    	List<UserEntity> userList = projectUserDomainService.getProjectUserListExceptUse(projectIdx);
    	
    	//Entity -> DTO 변환
    	return userList.stream().map(m -> UserDtoMapper.INSTANCE.toDto(m)).collect(Collectors.toList());
    }
    
    /**
     * Project 수정
     * @param
     * @return
     */
    public Boolean updateProject(ProjectRequestDto param) throws NotFoundProjectException, UpdateProjectFailException {
    	
    	String userId = param.getLoginId();
    	String userName = param.getLoginName();
    	String now = DateUtil.currentDateTime("yyyy-MM-dd hh:mm:ss");
    	
    	boolean result = false;
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	if(projectInfo == null) {
    		throw new NotFoundProjectException();
    	}
    	
    	try {
        	ProjectDtoBuilder projectBuiler = ProjectDto.builder();
        	projectBuiler.id(param.getProjectIdx());
        	projectBuiler.projectName(param.getProjectName());
        	projectBuiler.description(param.getDescription());
        	projectBuiler.createUserId(userId);
        	projectBuiler.createUserName(userName);
        	projectBuiler.createdAt(now);
        	projectBuiler.updateUserId(userId);
        	projectBuiler.updateUserName(userName);
        	projectBuiler.updatedAt(now);
        	projectBuiler.deletedYn(projectInfo.get().getDeletedYn());
        	
        	//ProjectDTO -> ProjectEntity
            ProjectEntity projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
            projectDomainService.updateProject(projectEntity);
        	
            //Project Cluster 삭제
            projectClusterDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
            
            //Project Cluster 등록
    		List<ProjectClusterDto> clusterList = param.getClusterList();
        	for(ProjectClusterDto cluster : clusterList) {
        		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
        		projectClusterBuiler.projectIdx(param.getProjectIdx());
        		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
        		
        		//ProjectClusterDTO -> ProjectClusterEntity
                ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                projectClusterDomainService.createProjectCluster(projectClusterEntity);
        	}
            
            //Project User 삭제
            projectUserDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
            
            //Project User 등록
            List<ProjectUserDto> userList = param.getUserList();
        	for(ProjectUserDto user : userList) {
        		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
        		projectUserBuiler.userId(user.getUserId());
        		projectUserBuiler.projectIdx(param.getProjectIdx());
        		projectUserBuiler.createUserId(userId);
        		projectUserBuiler.createUserName(userName);
        		projectUserBuiler.createdAt(now);
        		projectUserBuiler.projectUserRole(user.getProjectUserRole());
        		
        		//ProjectUserDTO -> ProjectUserEntity
                ProjectUserEntity projectUserEntity = ProjectUserDtoMapper.INSTANCE.toEntity(projectUserBuiler.build());
                projectUserDomainService.createProjectUser(projectUserEntity);
        	}
        	
        	result = true;
    	} catch(Exception e) {
    		throw new UpdateProjectFailException();
    	}
    	
    	return result;
    }
    
    /**
     * Project 삭제
     * @param
     * @return
     */
    public Boolean deleteProject(ProjectRequestDto param) throws NotFoundProjectException, AleadyUserClusterException, DeleteProjectFailException {
    	
    	boolean result = false;
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	if(projectInfo == null) {
    		throw new NotFoundProjectException();
    	}
    	
    	
		//Project 삭제시 삭제를 원하는 cluster 확인
    	List<ProjectClusterDto> clusterList = param.getClusterList();
    	if(clusterList != null) {
    		for(ProjectClusterDto dto : clusterList) {
    			//cluster 삭제시 다른 프로젝트에서 해당 클러스터를 사용하는 확인
    			List<ProjectClusterEntity> returnProjectList = projectClusterDomainService.getUseProjectByCluster(dto.getClusterIdx(), projectInfo.get().getId());
    			if(returnProjectList != null && returnProjectList.size() > 0) {
    				//다른 Project에서 해당 Cluster를 사용하고 있기 때문에 삭제 불가
    				throw new AleadyUserClusterException();
    			} else {
    				//Cluster 테이블 삭제 및 API 인터페이스를 통한 실제 Cluster 삭제
    			}
    		}
    	}
        	
        try {        	
        	//Project Cluster 삭제
            projectClusterDomainService.deleteProjectByProjectIdx(projectInfo.get().getId());
            
            //Project User 삭제
            projectUserDomainService.deleteProjectByProjectIdx(projectInfo.get().getId());
        	
        	//Project 삭제
        	projectDomainService.deleteProject(projectInfo.get().getId());
        	
        	result = true;
    	} catch(Exception e) {
    		throw new DeleteProjectFailException();
    	}
    	
    	return result;
    }
}
