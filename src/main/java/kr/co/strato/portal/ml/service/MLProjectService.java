package kr.co.strato.portal.ml.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.domain.machineLearning.service.MLProjectDomainService;
import kr.co.strato.domain.machineLearning.service.MLResourceDomainService;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLProjectDto;
import kr.co.strato.portal.ml.model.MLResourceDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class MLProjectService {
	
	@Autowired
	private MLProjectDomainService mlProjectDomainService;
	
	@Autowired
	private MLDomainService mlDomainService;
	
	@Autowired
	private MLResourceDomainService mlResourceDomainService;
	
	/**
	 * ML 프로젝트 리스트 조회.
	 * @param loginUser
	 * @param pageable
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Page<MLProjectDto> getList(UserDto loginUser, Pageable pageable, ProjectDto param) throws Exception {
		PageImpl<MLProjectDto> projectList = mlProjectDomainService.getProjectList(loginUser, pageable, param);
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

}
