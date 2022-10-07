package kr.co.strato.domain.machineLearning.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
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
	
	public List<MLClusterMappingEntity> getByClusterIdx(Long clusterIdx) {
		List<MLClusterMappingEntity> mlRes = mlClusterMappingRepository.findByClusterIdx(clusterIdx);
		return mlRes;
	}
	
	public List<MLClusterMappingEntity> getByMlIdx(Long mlIdx) {
		List<MLClusterMappingEntity> mlRes = mlClusterMappingRepository.findByMlIdx(mlIdx);
		return mlRes;
	}
	
	public void deleteByCluster(Long clusterIdx) {
		ClusterEntity entity = ClusterEntity.builder().clusterIdx(clusterIdx).build();
		deleteByCluster(entity);
	}
	
	public void deleteByCluster(ClusterEntity entity) {
		mlClusterMappingRepository.deleteByCluster(entity);
	}
	
	public void deleteByMlIdx(Long mlIdx) {
		mlClusterMappingRepository.deleteByMlIdx(mlIdx);
	}
	
}
