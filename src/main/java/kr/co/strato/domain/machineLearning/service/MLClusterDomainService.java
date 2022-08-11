package kr.co.strato.domain.machineLearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.repository.MLClusterRepository;
import kr.co.strato.global.model.PageRequest;

@Service
public class MLClusterDomainService {

	@Autowired
	private MLClusterRepository mlClusterRepository;
	
	public Long save(MLClusterEntity mlClusterEntity) {
		mlClusterRepository.save(mlClusterEntity);
		return mlClusterEntity.getId();
	}
	
	public MLClusterEntity get(ClusterEntity cluster) {
		Optional<MLClusterEntity> mlRes = mlClusterRepository.findByCluster(cluster);
		if (mlRes.isPresent()) {
			return mlRes.get();
		}
		return null;
	}
	
	public MLClusterEntity get(Long mlClusterIdx) {
		Optional<MLClusterEntity> mlRes = mlClusterRepository.findById(mlClusterIdx);
		if (mlRes.isPresent()) {
			return mlRes.get();
		}
		return null;
	}
	
	public List<MLClusterEntity> getList(String clusterType) {
		return mlClusterRepository.findByClusterType(clusterType);
	}
	
	public Page<MLClusterEntity> getList(String clusterType, PageRequest pageRequest) {
		return mlClusterRepository.findByClusterType(clusterType, pageRequest.of());
	}
	
	public void deleteByCluster(ClusterEntity cluster) {
		mlClusterRepository.deleteByCluster(cluster);
	}
	
	public void deleteByMlClusterIdx(Long mlClusterIdx) {
		mlClusterRepository.deleteByMlClusterIdx(mlClusterIdx);
	}
	
}
