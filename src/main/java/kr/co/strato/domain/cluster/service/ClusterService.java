package kr.co.strato.domain.cluster.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.Cluster;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class ClusterService {

	@Autowired
	ClusterRepository clusterRepository;
	
	
	public Long register(Cluster cluster) {
		clusterRepository.save(cluster);
		
		return cluster.getClusterIdx();
	}
	
	public void update(Cluster cluster) {
		clusterRepository.save(cluster);
	}
	
	public void delete(Cluster cluster) {
		clusterRepository.delete(cluster);
	}
	
	public Cluster get(Long clusterIdx) {
		Optional<Cluster> cluster = clusterRepository.findById(clusterIdx);
		if (cluster.isPresent()) {
			return cluster.get();
		} else {
			throw new NotFoundResourceException("cluster_idx : " + clusterIdx.toString());
		}
	}
	
	public Page<Cluster> getList(Pageable pageable) {
		return clusterRepository.findAll(pageable);
	}
}
