package kr.co.strato.domain.cluster.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
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

	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
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

	public boolean isClusterDuplication(String name) {
		Optional<ClusterEntity> cluster = clusterRepository.findByClusterName(name);
		if (cluster.isPresent()) {
			return true;
		}
		
		return false;
	}

	public List<ClusterEntity> getListByProjectIdx(Long projectIdx){
		List<ProjectClusterEntity> projectClusterEntities = projectClusterRepository.findByProjectIdx(projectIdx);
		List<ClusterEntity> clusters = projectClusterEntities.stream().map(e -> {
			Optional<ClusterEntity> cluster = clusterRepository.findById(e.getClusterIdx());
			if(cluster.isPresent()){
				return cluster.get();
			}
			return null;
		}).collect(Collectors.toList());

		return clusters;
	}
	
}
