package kr.co.strato.domain.machineLearning.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;

public interface CustomMLClusterRepository {
	
	public void deleteByMlClusterIdx(Long mlClusterIdx);
	
	public Page<MLClusterEntity> findByClusterType(String clusterType, Pageable pageable);

}
