package kr.co.strato.domain.cluster.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.ClusterEntity;

public interface ClusterRepository extends JpaRepository<ClusterEntity, Long> {

	public Optional<ClusterEntity> findByClusterName(String clusterName);
}
