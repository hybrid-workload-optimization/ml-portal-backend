package kr.co.strato.domain.machineLearning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;
import kr.co.strato.domain.machineLearning.repository.MLClusterMappingRepository;

@Service
public class MLClusterMappingDomainService {

	@Autowired
	private MLClusterMappingRepository mlClusterMappingRepository;
	
	public Long save(MLClusterMappingEntity mlClusterMappingEntity) {
		mlClusterMappingRepository.save(mlClusterMappingEntity);
		return mlClusterMappingEntity.getId();
	}
	
	public List<MLClusterMappingEntity> getByMlClusterIdx(Long mlClusterIdx) {
		List<MLClusterMappingEntity> mlRes = mlClusterMappingRepository.findByMlClusterIdx(mlClusterIdx);
		return mlRes;
	}
	
	public List<MLClusterMappingEntity> getByMlIdx(Long mlIdx) {
		List<MLClusterMappingEntity> mlRes = mlClusterMappingRepository.findByMlIdx(mlIdx);
		return mlRes;
	}
	
	public void deleteByMlClusterIdx(Long mlClusterIdx) {
		MLClusterEntity entity = MLClusterEntity.builder().id(mlClusterIdx).build();
		mlClusterMappingRepository.deleteByMlCluster(entity);
	}
	
	public void deleteByMlIdx(Long mlIdx) {
		mlClusterMappingRepository.deleteByMlIdx(mlIdx);
	}
	
}
