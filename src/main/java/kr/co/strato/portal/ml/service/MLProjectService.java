package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLClusterMappingDomainService;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLProjectDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.AleadyProjectNameException;
import kr.co.strato.global.error.exception.CreateProjectFailException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.ml.model.MLClusterDto;
import kr.co.strato.portal.ml.model.MLClusterDtoMapper;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLProjectDto;
import kr.co.strato.portal.ml.model.MLResourceDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectDto.ProjectDtoBuilder;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.mapper.ProjectDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class MLProjectService {
	
	@Autowired
	private MLProjectDomainService mlProjectDomainService;
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	@Autowired
	private MLClusterMappingDomainService mlClusterMappingDomainService;
	
	@Autowired
	private ProjectDomainService projectDomainService;
	
	
	
	/**
	 * ML 프로젝트 리스트 조회.
	 * @param loginUser
	 * @param pageable
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Page<MLProjectDto> getList(UserDto loginUser, Pageable pageable, String projectName) throws Exception {
		PageImpl<MLProjectDto> projectList = mlProjectDomainService.getProjectList(loginUser, pageable, projectName);
        return projectList;
	}
	
	/**
	 * 프로젝트 상세 정보 조회.
	 * @param projectIdx
	 * @return
	 */
	public MLProjectDto getProjectDetail(Long projectIdx) {
        return mlProjectDomainService.getProjectDetail(projectIdx);
    }
	
	/**
     * Project의 ML 리스트 조회
     * @param projectIdx
     * @return
     */
    public List<MLDto.Detail> getProjectMlList(Long projectIdx) {
    	List<MLEntity> list = mlProjectDomainService.getMlList(projectIdx);
    	
    	List<MLDto.Detail> result = new ArrayList<>();
    	for(MLEntity e: list) {
    		result.add(MLDtoMapper.INSTANCE.toDetailDto(e));
    	}    	
        return result;
    }
    
    /**
     * 프로젝트에 소속된 ML 클러스터 리스트 조회.
     * @param projectIdx
     * @return
     */
    public List<MLClusterDto.List> getProjectMlClusterList(Long projectIdx) {
    	List<MLEntity> list = mlProjectDomainService.getMlList(projectIdx);
    	List<MLClusterDto.List> clusters = new ArrayList<>();
    	for(MLEntity e: list) {
    		Long mlIdx = e.getId();
    		List<MLClusterMappingEntity> clusterMappings = mlClusterMappingDomainService.getByMlIdx(mlIdx);
    		for(MLClusterMappingEntity mapping : clusterMappings) {
    			MLClusterEntity mlClusterEntity = mapping.getMlCluster();
    			MLClusterDto.List l = MLClusterDtoMapper.INSTANCE.toListDto(mlClusterEntity);
    			clusters.add(l);
    		}
    	}
    	return clusters;
    }
    
    /**
     * ML 상세 정보 조회.
     * @param mlIdx
     * @return
     */
    public MLDto.Detail getMl(Long mlIdx) {
    	MLEntity entity = mlDomainService.get(mlIdx);
		
		List<MLResourceEntity> resEntitys = mlResourceDomainService.getList(entity.getId());		
		List<MLResourceDto> resources = new ArrayList<>();
		for(MLResourceEntity resEntity : resEntitys) {			
			MLResourceDto resDto = MLDtoMapper.INSTANCE.toResDto(resEntity);
			resources.add(resDto);
		}	
		
		MLDto.Detail detail = MLDtoMapper.INSTANCE.toDetailDto(entity);
		detail.setResources(resources);
		return detail;
    }
    
    
    /**
     * Project 생성
     * @param
     * @return
     */
    public ProjectEntity createProject(ProjectRequestDto param) throws AleadyProjectNameException, CreateProjectFailException {
    	String projectName = param.getProjectName();    	
    	Optional<ProjectEntity> projectInfo = projectDomainService.getProjectByProjectName(projectName, "N");
    	ProjectEntity projectEntity = null;
     	if(!projectInfo.isPresent()) {
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
     		
     		projectEntity = ProjectDtoMapper.INSTANCE.toEntity(projectBuiler.build());
     		projectDomainService.createProject(projectEntity);
 		} else {
 			projectEntity = projectInfo.get();
 		}    	
    	
    	return projectEntity;
    }

}
