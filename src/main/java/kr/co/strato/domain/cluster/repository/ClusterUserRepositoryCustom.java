package kr.co.strato.domain.cluster.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.portal.setting.model.UserDto;

public interface ClusterUserRepositoryCustom {
	
	/**
	 * User가 접근 가능한 클러스터 리스트 반환.
	 * @param userId
	 * @return
	 */
	public List<ClusterEntity> getUserClusterList(UserDto loginUser);
	
	
	public Page<ClusterEntity> getUserClusterList(Pageable pageable, UserDto loginUser);
	
	
	public List<UserEntity> getClusterUsers(Long clusterIdx);
	
}