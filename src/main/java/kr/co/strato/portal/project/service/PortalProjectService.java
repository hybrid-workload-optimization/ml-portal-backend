package kr.co.strato.portal.project.service;

import java.util.ArrayList;
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
import kr.co.strato.global.error.exception.AleadyProjectNameException;
import kr.co.strato.global.error.exception.AleadyUserClusterException;
import kr.co.strato.global.error.exception.CreateProjectFailException;
import kr.co.strato.global.error.exception.DeleteProjectFailException;
import kr.co.strato.global.error.exception.NotFoundProjectException;
import kr.co.strato.global.error.exception.UpdateProjectFailException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import kr.co.strato.portal.cluster.service.ClusterService;
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
	
	@Autowired
	ClusterService clusterService;
	
	/**
     * Project 리스트 조회
     * @param pageable
     * @param param
     * @return
     */
    public Page<ProjectDto> getProjectList(UserDto loginUser, Pageable pageable, ProjectDto param) throws Exception {
    	
    	PageImpl<ProjectDto> projectList = projectDomainService.getProjectList(loginUser, pageable, param);
    	
    	//페이징 정보 추가
        return projectList;
    }
    
    /**
     * Project 상세 조회
     * @param projectIdx
     * @return
     */
    public ProjectDto getProjectDetail(Long projectIdx, String type) {
    	
        return projectDomainService.getProjectDetail(projectIdx, type);
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
     * 프로젝트로 등록하지 않은 Cluster 리스트 조회
     * @param loginId
     * @return
     */
    public List<ClusterDto.List> getProjecClusterListByNotUsedClusters() {
    	
    	List<ClusterEntity> clusterList = projectClusterDomainService.getProjecClusterListByNotUsedClusters();
    	
    	//Entity -> DTO 변환
    	return clusterList.stream().map(m -> ClusterDtoMapper.INSTANCE.toList(m)).collect(Collectors.toList());
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
    public Long createProject(ProjectRequestDto param) throws AleadyProjectNameException, CreateProjectFailException {
    	
    	String userId = param.getLoginId();
    	String userName = param.getLoginName();
    	String now = DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss");
    	
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
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectByProjectName(param.getProjectName(), "N");
		if(projectInfo.isPresent()) {
			throw new AleadyProjectNameException();
		}
    	
		Long resultIdx = null;
    	try {
    		//ProjectDTO -> ProjectEntity
            ProjectEntity projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
            resultIdx = projectDomainService.createProject(projectEntity);
        	
            if(resultIdx == null) {
            	throw new CreateProjectFailException();
            } else {
            	//Project Cluster 등록
        		if(param.getClusterList() != null) {
        			List<ProjectClusterDto> clusterList = param.getClusterList();
                	for(ProjectClusterDto cluster : clusterList) {
                		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
                		projectClusterBuiler.projectIdx(resultIdx);
                		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
                		
                		//ProjectClusterDTO -> ProjectClusterEntity
                        ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                        projectClusterDomainService.createProjectCluster(projectClusterEntity);
                	}
        		}
            	
            	//Project User 등록
        		if(param.getUserList() != null) {
        			List<ProjectUserDto> userList = param.getUserList();
                	for(ProjectUserDto user : userList) {
                		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
                		projectUserBuiler.userId(user.getUserId());
                		projectUserBuiler.projectIdx(resultIdx);
                		projectUserBuiler.createUserId(userId);
                		projectUserBuiler.createUserName(userName);
                		projectUserBuiler.createdAt(now);
                		projectUserBuiler.userRoleIdx(user.getUserRoleIdx());
                		//projectUserBuiler.projectUserRole(user.getProjectUserRole());
                		
                		//ProjectUserDTO -> ProjectUserEntity
                        ProjectUserEntity projectUserEntity = ProjectUserDtoMapper.INSTANCE.toEntity(projectUserBuiler.build());
                        projectUserDomainService.createProjectUser(projectUserEntity);
                	}
        		}
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new CreateProjectFailException();
    	}
    	
    	return resultIdx;
    }
    
    /**
     * Project에서 사용중인 Cluster를 제외한 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<ClusterDto.List> getProjectClusterListExceptUse(Long projectIdx) {
    	
    	List<ClusterEntity> clusterList = projectClusterDomainService.getProjectClusterListExceptUse(projectIdx);
    	
    	//Entity -> DTO 변환
    	return clusterList.stream().map(m -> ClusterDtoMapper.INSTANCE.toList(m)).collect(Collectors.toList());
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
     * Project 상세 정보 수정
     * @param
     * @return
     */
    public Boolean updateProject(ProjectRequestDto param) throws NotFoundProjectException, UpdateProjectFailException {
    	
    	String userId = param.getLoginId();
    	String userName = param.getLoginName();
    	String now = DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss");
    	
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
        	
/*            
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
*/        	
        	
        	result = true;
    	} catch(Exception e) {
    		throw new UpdateProjectFailException();
    	}
    	
    	return result;
    }
    
    /**
     * Project Cluster 수정
     * @param
     * @return
     */
    public Boolean updateProjectCluster(ProjectRequestDto param) throws NotFoundProjectException, UpdateProjectFailException {
    	
    	boolean result = false;
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	if(projectInfo == null) {
    		throw new NotFoundProjectException();
    	}
    	
    	try {
            //Project Cluster 삭제
            //projectClusterDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
    		List<Long> duplicateIdxList = new ArrayList<Long>();
    		List<ProjectClusterDto> clusterList = projectClusterDomainService.getProjectClusterList(param.getProjectIdx());
    		List<ProjectClusterDto> reqClusterList = param.getClusterList();
    		for(ProjectClusterDto nowCluster : clusterList) {
    			for(ProjectClusterDto reqCluster : reqClusterList) {
    				if(nowCluster.getClusterIdx().equals(reqCluster.getClusterIdx())) {
    					duplicateIdxList.add(reqCluster.getClusterIdx());
    					break;
    				}
    			}
    		}
    		
    		if(duplicateIdxList != null && duplicateIdxList.size() > 0) {
    			/*for(Long idx : duplicateIdxList) {
    				System.out.println("clusterIdx ===" + idx);
    			}*/
    			projectClusterDomainService.deleteRequestProjectCluster(param.getProjectIdx(), duplicateIdxList);
    		}
            
            //Project Cluster 등록
    		reqClusterList = param.getClusterList();
        	for(ProjectClusterDto cluster : reqClusterList) {
        		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
        		projectClusterBuiler.projectIdx(param.getProjectIdx());
        		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
        		
        		ProjectClusterEntity selectCluster = projectClusterDomainService.getProjectCluster(param.getProjectIdx(), cluster.getClusterIdx());
        		if(selectCluster != null) {
        			System.out.println("selectCluster === " + selectCluster.getClusterIdx());
        			continue;
        		}
        		
        		//ProjectClusterDTO -> ProjectClusterEntity
                ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                projectClusterDomainService.createProjectCluster(projectClusterEntity);
        	}
        	
        	result = true;
    	} catch(Exception e) {
    		throw new UpdateProjectFailException();
    	}
    	
    	return result;
    }
    
    /**
     * Project User 수정
     * @param
     * @return
     */
    public Boolean updateProjectUser(ProjectRequestDto param) throws NotFoundProjectException, UpdateProjectFailException {
    	
    	String userId = param.getLoginId();
    	String userName = param.getLoginName();
    	String now = DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss");
    	
    	boolean result = false;
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	if(projectInfo == null) {
    		throw new NotFoundProjectException();
    	}
    	
    	try {
            //Project User 삭제
            //projectUserDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
    		List<String> duplicateIdList = new ArrayList<String>();
    		List<ProjectUserDto> userList = projectUserDomainService.getProjectUserList(param.getProjectIdx());
    		List<ProjectUserDto> reqUserList = param.getUserList();
    		for(ProjectUserDto nowUser : userList) {
    			for(ProjectUserDto reqCluster : reqUserList) {
    				if(nowUser.getUserId().equals(reqCluster.getUserId())) {
    					duplicateIdList.add(reqCluster.getUserId());
    					break;
    				}
    			}
    		}
    		
    		if(duplicateIdList != null && duplicateIdList.size() > 0) {
    			/*for(String id : duplicateIdList) {
    				System.out.println("userId ===" + id);
    			}*/
    			projectUserDomainService.deleteRequestProjectUser(param.getProjectIdx(), duplicateIdList);
    		}
            
            //Project User 등록
            reqUserList = param.getUserList();
        	for(ProjectUserDto user : reqUserList) {
        		/*ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
        		projectUserBuiler.userId(user.getUserId());
        		projectUserBuiler.projectIdx(param.getProjectIdx());
        		projectUserBuiler.createUserId(userId);
        		projectUserBuiler.createUserName(userName);
        		projectUserBuiler.createdAt(now);
        		projectUserBuiler.projectUserRole(user.getProjectUserRole());*/
        		
        		ProjectUserEntity selectUser = projectUserDomainService.getProjectUser(param.getProjectIdx(), user.getUserId());
        		
        		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
        		projectUserBuiler.userId(user.getUserId());
        		projectUserBuiler.projectIdx(param.getProjectIdx());
        		projectUserBuiler.createUserId(userId);
        		projectUserBuiler.createUserName(userName);
        		projectUserBuiler.userRoleIdx(user.getUserRoleIdx());
        		//projectUserBuiler.projectUserRole(user.getProjectUserRole());
        		if(selectUser != null) {
        			System.out.println("selectUser === " + selectUser.getUserId());
        			projectUserBuiler.createdAt(selectUser.getCreatedAt());
        		} else {
            		projectUserBuiler.createdAt(now);
        		}
        		
        		//ProjectUserDTO -> ProjectUserEntity
                ProjectUserEntity projectUserEntity = ProjectUserDtoMapper.INSTANCE.toEntity(projectUserBuiler.build());
        		projectUserDomainService.createProjectUser(projectUserEntity);
        	}
        	
        	result = true;
    	} catch(Exception e) {
    		e.printStackTrace();
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
    		System.out.println("삭제 대상 Cluster 총 개수 === " + clusterList.size());
    		for(ProjectClusterDto dto : clusterList) {
    			System.out.println("삭제 대상 Cluster Idx === " + dto.getClusterIdx());
    			//cluster 삭제시 다른 프로젝트에서 해당 클러스터를 사용하는 확인
    			List<ProjectClusterEntity> returnProjectList = projectClusterDomainService.getUseProjectByCluster(dto.getClusterIdx(), projectInfo.get().getId());
    			if(returnProjectList != null && returnProjectList.size() > 0) {
    				//다른 Project에서 해당 Cluster를 사용하고 있기 때문에 삭제 불가
    				throw new AleadyUserClusterException();
    			} else {
    				//Cluster 테이블 삭제 및 API 인터페이스를 통한 실제 Cluster 삭제
    				try {
    					clusterService.deleteCluster(dto.getClusterIdx());
    				} catch(Exception e) {
    					throw new DeleteProjectFailException();
    				}
    				
    			}
    		}
    	}
        	
        try {        	
        	//Project Cluster 삭제
            projectClusterDomainService.deleteProjectByProjectIdx(projectInfo.get().getId());
            
            //Project User 삭제
            projectUserDomainService.deleteProjectByProjectIdx(projectInfo.get().getId());
        	
        	//Project 삭제
            String userId = param.getLoginId();
        	String userName = param.getLoginName();
        	String now = DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss");
            
            ProjectEntity projectEntity = projectInfo.get();
            projectEntity.setUpdateUserId(userId);
            projectEntity.setUpdateUserName(userName);
            projectEntity.setUpdatedAt(now);
            projectEntity.setDeletedYn("Y");
            projectDomainService.deleteProject(projectEntity);
        	
        	result = true;
    	} catch(Exception e) {
    		throw new DeleteProjectFailException();
    	}
    	
    	return result;
    }
    
    /**
     * Project Cluster 삭제
     * @param
     * @return
     */
    public Boolean deleteProjectCluster(Long projectIdx, Long clusterIdx)  {
    	
    	Boolean result = false;
    	
    	//Project Cluster 삭제
        int deleteResult = projectClusterDomainService.deleteProjectCluster(projectIdx, clusterIdx);
        if(deleteResult > 0) {
        	result = true;
        }
        
        return result;
    }
    
    /**
     * Project User 삭제
     * @param
     * @return
     */
    public Boolean deleteProjectUser(Long projectIdx, String userId)  {
    	
    	Boolean result = false;
    	
    	//Project User 삭제
        int deleteResult = projectUserDomainService.deleteProjectUser(projectIdx, userId);
        if(deleteResult > 0) {
        	result = true;
        }
        
        return result;
    }
    
    /**
    * ClusterId가 소속 된 Project 상세 조회
    * @param clusterIdx : ClusterId
    * @return ProjectDto 반환, 소속된 Project가 없는 경우 null 반환
    */
    public ProjectDto getProjectDetailByClusterId(Long clusterIdx) {
    	
    	ProjectEntity result = projectDomainService.getProjectDetailByClusterId(clusterIdx);
    	
    	//ProjectEntity -> ProjectDto
    	return ProjectDtoMapper.INSTANCE.toDto(result);
    }
}
