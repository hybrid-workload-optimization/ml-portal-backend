package kr.co.strato.domain.machineLearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterEntity;

public interface MLClusterRepository extends JpaRepository<MLClusterEntity, Long>, CustomMLClusterRepository {
	
	public List<MLClusterEntity> findByClusterType(String clusterType);

	public Optional<MLClusterEntity> findByCluster(ClusterEntity cluster);
	
	@Transactional
	public void deleteByCluster(ClusterEntity cluster);
}
