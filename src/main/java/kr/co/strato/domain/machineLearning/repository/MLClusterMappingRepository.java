package kr.co.strato.domain.machineLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;

public interface MLClusterMappingRepository extends JpaRepository<MLClusterMappingEntity, Long>, CustomMLClusterMappingRepository {
	
}
