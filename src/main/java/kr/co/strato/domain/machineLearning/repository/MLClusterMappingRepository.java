package kr.co.strato.domain.machineLearning.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;

public interface MLClusterMappingRepository extends JpaRepository<MLClusterMappingEntity, Long>, CustomMLClusterMappingRepository {

	@Transactional
	public void deleteByMlCluster(MLClusterEntity mlCluster);	
}
