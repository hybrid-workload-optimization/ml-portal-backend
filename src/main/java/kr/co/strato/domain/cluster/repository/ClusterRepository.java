package kr.co.strato.domain.cluster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.ClusterEntity;

public interface ClusterRepository extends JpaRepository<ClusterEntity, Long>, ClusterUserRepositoryCustom {

	public Optional<ClusterEntity> findByClusterName(String clusterName);
	
	public List<ClusterEntity> findByCreateUserId(String loginId);
}
