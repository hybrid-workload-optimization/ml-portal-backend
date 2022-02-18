package kr.co.strato.domain.cluster.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class ClusterDomainService {

	@Autowired
	ClusterRepository clusterRepository;
	
	
	public void register(ClusterEntity clusterEntity) {
		clusterRepository.save(clusterEntity);
	}
	
	public void update(ClusterEntity clusterEntity) {
		Optional<ClusterEntity> cluster = clusterRepository.findById(clusterEntity.getClusterIdx());
		if (cluster.isPresent()) {
			clusterRepository.save(clusterEntity);
		} else {
			throw new NotFoundResourceException("cluster_idx : " + clusterEntity.getClusterIdx());
		}
	}
	
	public void delete(ClusterEntity clusterEntity) {
		clusterRepository.delete(clusterEntity);
	}
	
	public ClusterEntity get(Long clusterIdx) {
		Optional<ClusterEntity> cluster = clusterRepository.findById(clusterIdx);
		if (cluster.isPresent()) {
			return cluster.get();
		} else {
			throw new NotFoundResourceException("cluster_idx : " + clusterIdx.toString());
		}
	}
	
	public Page<ClusterEntity> getList(Pageable pageable) {
		return clusterRepository.findAll(pageable);
	}
}
