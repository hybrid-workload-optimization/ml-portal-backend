package kr.co.strato.domain.machineLearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLProjectMappingEntity;
import kr.co.strato.domain.machineLearning.repository.MLProjectRepository;
import kr.co.strato.portal.ml.model.MLProjectDto;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class MLProjectDomainService {

	@Autowired
	private MLProjectRepository mlProjectRepository;
	
	public Long save(MLProjectMappingEntity mlProjectEntity) {
		mlProjectRepository.save(mlProjectEntity);
		return mlProjectEntity.getId();
	}
	
	public void deleteByMlId(String mlId) {
		MLEntity entity = new MLEntity();
		entity.setId(null);
		mlProjectRepository.deleteByMl(entity);
	}
	
	/**
	 * 프로젝트 리스트 조회.
	 * @param loginUser
	 * @param pageable
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public PageImpl<MLProjectDto> getProjectList(UserDto loginUser, Pageable pageable, String projectName) throws Exception {
    	return mlProjectRepository.getProjectList(loginUser, pageable, projectName);
    }
	
	/**
     * Project 상세 조회
     * @param projectIdx
     * @return
     */
    public MLProjectDto getProjectDetail(Long projectIdx) {
    	return mlProjectRepository.getProjectDetail(projectIdx);
    }
    
    /**
     * 프로젝트에 소속된 ML 리스트 조회.
     * @param projectIdx
     * @return
     */
    public List<MLEntity> getMlList(Long projectIdx) {
    	return mlProjectRepository.getProjectMlList(projectIdx);
    }
	
}
