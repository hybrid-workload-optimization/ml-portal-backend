package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;

public interface CustomMLClusterMappingRepository {
	
	public List<MLClusterMappingEntity> findByMlClusterIdx(Long mlClusterIdx);
	public List<MLClusterMappingEntity> findByMlIdx(Long mlIdx);
	
	
	public void deleteByMlIdx(Long mlIdx);

}
