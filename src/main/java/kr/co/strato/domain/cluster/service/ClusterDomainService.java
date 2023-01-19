package kr.co.strato.domain.cluster.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.mapper.ClusterMapper;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class ClusterDomainService {

	@Autowired
	ClusterRepository clusterRepository;

	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
	@Autowired
    ClusterMapper clusterMapper;
	
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
	
	public ClusterEntity get(Long clusterIdx) {
		Optional<ClusterEntity> cluster = clusterRepository.findById(clusterIdx);
		if (cluster.isPresent()) {
			return cluster.get();
		} else {
			throw new NotFoundResourceException("cluster_idx : " + clusterIdx.toString());
		}
	}
	
	public ClusterEntity getNullable(Long clusterIdx) {
		Optional<ClusterEntity> cluster = clusterRepository.findById(clusterIdx);
		if (cluster.isPresent()) {
			return cluster.get();
		} 
		return null;
	}
	
	public Page<ClusterEntity> getList(UserDto loginUser, Pageable pageable) {
		return clusterRepository.getUserClusterList(pageable, loginUser);
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

	public List<ClusterEntity> getListAll(){
		List<ClusterEntity> clusters = clusterRepository.findAll();

		return clusters;
	}
	
	/**
	 * 로그인한 사용자가 접근 가능한 클러스터 리스트 반환.
	 * @param loginUser
	 * @return
	 */
	public List<ClusterEntity> getListByLoginUser(UserDto loginUser){
		return clusterRepository.getUserClusterList(loginUser);
	}
	
	public List<ClusterEntity> getListByLoginUserForDevops(UserDto loginUser){
		return clusterRepository.getUserClusterListForDevops(loginUser);
	}
	

	public void delete(ClusterEntity clusterEntity) {
		clusterMapper.deleteClusterAll(clusterEntity.getClusterIdx());
	}
	
	public ClusterEntity findByClusterName(String clusterName) {
		Optional<ClusterEntity> cluster = clusterRepository.findByClusterName(clusterName);
		if (cluster.isPresent()) {
			return cluster.get();
		}
		return null;
	}
	
	/**
	 * 클러스터와 관계 있는 유저 목록 반환.
	 * @param clsuterIdx
	 * @return
	 */
	public List<UserEntity> getClusterUsers(Long clusterIdx) {
		return clusterRepository.getClusterUsers(clusterIdx);
	}
	
}
