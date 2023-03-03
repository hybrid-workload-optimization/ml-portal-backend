package kr.co.strato.portal.project.service;

import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

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
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserRoleDomainService;
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
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.common.service.CommonService;
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
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserRoleDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PortalProjectService extends CommonController {

	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectUserDomainService projectUserDomainService;
	
	@Autowired
	ProjectClusterDomainService projectClusterDomainService;
	
	@Autowired
	ClusterService clusterService;
	
	
	@Autowired
	private UserRoleDomainService userRoleDomainService;
	
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
    
    public Long getProjectIdx(String projectName) {
    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectByProjectName(projectName, "N");
    	
    	return projectInfo.get().getId();
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
     * Project의 User 리스트 조회(Project Manager 제외)
     * @param projectIdx
     * @return
     */
    public List<ProjectUserDto> getProjectUserListExceptManager(Long projectIdx) {
    	
        return projectUserDomainService.getProjectUserListExceptManager(projectIdx);
    }
    
    /**
     * 프로젝트로 등록하지 않은 Cluster 리스트 조회
     * @param loginId
     * @return
     */
    public List<ClusterDto.List> getProjecClusterListByNotUsedClusters() {
    	
    	List<ClusterEntity> clusterList = projectClusterDomainService.getProjecClusterListByNotUsedClusters(getLoginUser());
    	
    	//Entity -> DTO 변환
    	return clusterList.stream().map(m -> ClusterDtoMapper.INSTANCE.toList(m)).collect(Collectors.toList());
    }
    
    /**
     * 프로젝트에 추가할 수 있는 사용자 리트는 반환.
     * @return
     */
    public List<UserDto> getAvailableProjectUserList() {
    	List<UserEntity> userList = projectUserDomainService.getAvailableProjectUserList(getLoginUser());
    	
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
    	
    	Optional<ProjectEntity> projectInfo = null;
    	
    	if(param.getProjectIdx() != null) {
    		projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	} else {
    		projectInfo = projectDomainService.getProjectByProjectName(param.getProjectName(), "N");
    	}
    	
    	if(projectInfo == null) {
    		throw new NotFoundProjectException();
    	}
    	
    	try {
    		//Project General
    		ProjectEntity oldEntity = projectInfo.get();
    		
        	ProjectDtoBuilder projectBuiler = ProjectDto.builder();
        	projectBuiler.id(projectInfo.get().getId());
        	projectBuiler.projectName(param.getProjectName());
        	projectBuiler.description(param.getDescription());
        	projectBuiler.createUserId(userId);
        	projectBuiler.createUserName(userName);
        	projectBuiler.createdAt(oldEntity.getCreatedAt());
        	projectBuiler.updateUserId(userId);
        	projectBuiler.updateUserName(userName);
        	projectBuiler.updatedAt(now);
        	projectBuiler.deletedYn(projectInfo.get().getDeletedYn());
        	
        	//ProjectDTO -> ProjectEntity
            ProjectEntity projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
            projectDomainService.updateProject(projectEntity);
        	
            List<String> duplicateIdList = new ArrayList<String>();
            
            // Project Manager 정보 확인 및 수정
        	ProjectUserDto managerInfo = param.getProjectManager();
        	if(managerInfo != null) {
        		ProjectUserEntity pmUserInfo = projectUserDomainService.getProjectManagerInfo(projectInfo.get().getId());
        		ProjectUserDto pmUser = ProjectUserDtoMapper.INSTANCE.toDto(pmUserInfo);
        		if(!managerInfo.getUserId().equals(pmUser.getUserId())) {
    				
    				// 신규 Project Manager 적용
    				ProjectUserDtoBuilder newProjectManagerBuiler = ProjectUserDto.builder();
    				newProjectManagerBuiler.userId(managerInfo.getUserId());
    				newProjectManagerBuiler.projectIdx(projectInfo.get().getId());
    				newProjectManagerBuiler.createUserId(userId);
    				newProjectManagerBuiler.createUserName(userName);
    				newProjectManagerBuiler.createdAt(now);
    				newProjectManagerBuiler.userRoleIdx(ProjectUserEntity.PROJECT_MANAGER_ROLE_IDX);

            		//ProjectUserDTO -> ProjectUserEntity
                    ProjectUserEntity newProjectManagerEntity = ProjectUserDtoMapper.INSTANCE.toEntity(newProjectManagerBuiler.build());
    				projectUserDomainService.createProjectUser(newProjectManagerEntity);
    				
    				// 기존 Project Manager 를 Member로 변경
    				ProjectUserDtoBuilder oldProjectManagerBuiler = ProjectUserDto.builder();
    				oldProjectManagerBuiler.userId(pmUser.getUserId());
    				oldProjectManagerBuiler.projectIdx(param.getProjectIdx());
    				oldProjectManagerBuiler.createUserId(userId);
    				oldProjectManagerBuiler.createUserName(userName);
    				oldProjectManagerBuiler.createdAt(now);
    				oldProjectManagerBuiler.userRoleIdx(ProjectUserEntity.PROJECT_MEMBER_ROLE_IDX);
    				
    				//ProjectUserDTO -> ProjectUserEntity
    				ProjectUserEntity oldProjectManagerEntity = ProjectUserDtoMapper.INSTANCE.toEntity(oldProjectManagerBuiler.build());
    				projectUserDomainService.createProjectUser(oldProjectManagerEntity);
    				
    				duplicateIdList.add(pmUser.getUserId()); // 기존 PM 이 삭제되지 않도록 List 에 추가 
    			}
        	}
        	
        	//Project Cluster
        	List<ProjectClusterDto> reqClusterList = param.getClusterList();
    		if(reqClusterList != null && !reqClusterList.isEmpty()) {
    			List<Long> duplicateIdxList = new ArrayList<Long>();
        		List<ProjectClusterDto> clusterList = projectClusterDomainService.getProjectClusterList(projectInfo.get().getId());
        		for(ProjectClusterDto nowCluster : clusterList) {
        			for(ProjectClusterDto reqCluster : reqClusterList) {
        				if(nowCluster.getClusterIdx().equals(reqCluster.getClusterIdx())) {
        					duplicateIdxList.add(reqCluster.getClusterIdx());
        					break;
        				}
        			}
        		}
        		
        		if(duplicateIdxList != null && duplicateIdxList.size() > 0) {
        			projectClusterDomainService.deleteRequestProjectCluster(projectInfo.get().getId(), duplicateIdxList);
        		}
                
                //Project Cluster 등록
        		reqClusterList = param.getClusterList();
            	for(ProjectClusterDto cluster : reqClusterList) {
            		ProjectClusterDtoBuilder projectClusterBuiler = ProjectClusterDto.builder();
            		projectClusterBuiler.projectIdx(projectInfo.get().getId());
            		projectClusterBuiler.clusterIdx(cluster.getClusterIdx());
            		
            		ProjectClusterEntity selectCluster = projectClusterDomainService.getProjectCluster(projectInfo.get().getId(), cluster.getClusterIdx());
            		if(selectCluster != null) {
            			System.out.println("selectCluster === " + selectCluster.getClusterIdx());
            			continue;
            		}
            		
            		//ProjectClusterDTO -> ProjectClusterEntity
                    ProjectClusterEntity projectClusterEntity = ProjectClusterDtoMapper.INSTANCE.toEntity(projectClusterBuiler.build());
                    projectClusterDomainService.createProjectCluster(projectClusterEntity);
            	}
    		} else {
    			projectClusterDomainService.deleteProjectByProjectIdx(projectInfo.get().getId());
    		}
    		
    		//Project Member
    		List<ProjectUserDto> reqUserList = param.getUserList();
    		List<ProjectUserDto> userList = null;
    		
    		System.out.println("request === " + reqUserList);
    		
    		//Project User 삭제
    		if(reqUserList != null && !reqUserList.isEmpty()) {
        		//List<String> duplicateIdList = new ArrayList<String>();
        		
        		ProjectUserEntity pmUserInfo = projectUserDomainService.getProjectManagerInfo(projectInfo.get().getId());
        		duplicateIdList.add(pmUserInfo.getUserId());
        		
        		userList = projectUserDomainService.getProjectUserListExceptManager(projectInfo.get().getId());
        		for(ProjectUserDto nowUser : userList) {
        			for(ProjectUserDto reqCluster : reqUserList) {
        				if(nowUser.getUserId().equals(reqCluster.getUserId())) {
        					duplicateIdList.add(reqCluster.getUserId());
        					break;
        				}
        			}
        		}
        		
        		if(duplicateIdList != null && duplicateIdList.size() > 0) {
        			projectUserDomainService.deleteRequestProjectUser(projectInfo.get().getId(), duplicateIdList);
        		}
                
                //Project User 등록
        		for(ProjectUserDto user : reqUserList) {
             		ProjectUserEntity selectUser = projectUserDomainService.getProjectUser(projectInfo.get().getId(), user.getUserId());
            		
            		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
            		projectUserBuiler.userId(user.getUserId());
            		projectUserBuiler.projectIdx(projectInfo.get().getId());
            		projectUserBuiler.createUserId(userId);
            		projectUserBuiler.createUserName(userName);
            		projectUserBuiler.userRoleIdx(user.getUserRoleIdx());
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
    		} else {
    			projectUserDomainService.deleteProjectUserExceptManager(projectInfo.get().getId());
    		}
            
            result = true;
    	} catch(Exception e) {
    		e.printStackTrace();
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
    		List<ProjectClusterDto> reqClusterList = param.getClusterList();
    		
    		System.out.println("select cluster === " + reqClusterList);    		
    		
    		if(!reqClusterList.isEmpty()) {
    			List<Long> duplicateIdxList = new ArrayList<Long>();
        		List<ProjectClusterDto> clusterList = projectClusterDomainService.getProjectClusterList(param.getProjectIdx());
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
    		} else {
    			projectClusterDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
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
    	
    	Optional<ProjectEntity> projectInfo = null;
    	
    	if(param.getProjectIdx() != null) {
    		projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	} else {
    		projectInfo = projectDomainService.getProjectByProjectName(param.getProjectName(), "N");
    	}
    	
    	try {
            //Project User 삭제
    		List<ProjectUserDto> reqUserList = param.getUserList();
    		if(!reqUserList.isEmpty()) {
    			//projectUserDomainService.deleteProjectByProjectIdx(param.getProjectIdx());
        		List<String> duplicateIdList = new ArrayList<String>();
        		
        		ProjectUserEntity pmUserInfo = projectUserDomainService.getProjectManagerInfo(projectInfo.get().getId());
        		duplicateIdList.add(pmUserInfo.getUserId());
        		
        		List<ProjectUserDto> userList = projectUserDomainService.getProjectUserListExceptManager(projectInfo.get().getId());
        		
        		for(ProjectUserDto nowUser : userList) {
        			for(ProjectUserDto reqCluster : reqUserList) {
        				if(nowUser.getUserId().equals(reqCluster.getUserId())) {
        					duplicateIdList.add(reqCluster.getUserId());
        					break;
        				}
        			}
        		}
        		
        		if(duplicateIdList != null && duplicateIdList.size() > 0) {
//        			projectUserDomainService.deleteRequestProjectUser(projectInfo.get().getId(), duplicateIdList);
        		}
                
                //Project User 등록
        		for(ProjectUserDto user : reqUserList) {
            		ProjectUserEntity selectUser = projectUserDomainService.getProjectUser(projectInfo.get().getId(), user.getUserId());
            		
            		ProjectUserDtoBuilder projectUserBuiler = ProjectUserDto.builder();
            		projectUserBuiler.userId(user.getUserId());
            		projectUserBuiler.projectIdx(projectInfo.get().getId());
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
    		} else {
    			projectUserDomainService.deleteProjectUserExceptManager(projectInfo.get().getId());
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
    public Boolean deleteProject(ProjectRequestDto param, UserDto loginUser) throws NotFoundProjectException, AleadyUserClusterException, DeleteProjectFailException {
    	
    	boolean result = false;
    	
    	Optional<ProjectEntity> projectInfo = null;
    	if(param.getProjectIdx() != null) {
    		projectInfo = projectDomainService.getProjectById(param.getProjectIdx());
    	} else {
    		projectInfo = projectDomainService.getProjectByProjectName(param.getProjectName(), "N");
    	}
    	
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
    					clusterService.deleteCluster(dto.getClusterIdx(), loginUser);
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
    
    /**
	 * 프로젝트 사용자가 가질 수 있는 유저 권한 리턴.
	 * @return
	 */
	public List<UserRoleDto> getProjectUserRole() {
		List<UserRoleEntity> list =  userRoleDomainService.getProjectUserRole();
		List<UserRoleDto> roleList = list
				.stream()
				.map(r -> UserRoleDtoMapper.INSTANCE.toDto(r))
				.collect(Collectors.toList());
		return roleList;
	}
	
	/**
	 * 사용자가 유저 모든 권한
	 * @return
	 */
	public List<UserRoleDto> getUserRoleAll() {
		List<UserRoleEntity> list =  userRoleDomainService.getUseUserRole();
		List<UserRoleDto> roleList = list
				.stream()
				.map(r -> UserRoleDtoMapper.INSTANCE.toDto(r))
				.collect(Collectors.toList());
		return roleList;
	}
	
	/**
     * 사용자 권한이 Project Manager 인 사용자 조회
     * @return
     */
    public List<UserDto> getUserWithManagerList(UserDto loginUser, Long projectIdx) {
    	List<UserDto> userList = null;
    	if(loginUser != null) {
    		String roleCode = loginUser.getUserRole().getUserRoleCode();
    		
    		if(roleCode.equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN)
    				|| roleCode.equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
    			
    			//Admin 권한인 경우 프로젝트메니저 권한을 가진 모든 사용자 반환.
    			List<UserEntity> list = projectUserDomainService.getUserWithManagerList(projectIdx);
    			
    			//Entity -> DTO 변환
    			userList = list.stream().map(m -> UserDtoMapper.INSTANCE.toDto(m)).collect(Collectors.toList());
    		} else if(roleCode.equals(UserRoleEntity.ROLE_CODE_PROJECT_MANAGER)) {
    			//프로젝트 메니저 권한을 가진 사용자인 경우 본인을 반환.
    			userList = new ArrayList<>();
    			userList.add(loginUser);
    		} else {
    			userList = new ArrayList<>();
    		}
    		
    	}
    	return userList;
    }
    
    /**
     * Project의 User 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
    	
        return projectUserDomainService.getProjectUserList(projectIdx);
    }
}
