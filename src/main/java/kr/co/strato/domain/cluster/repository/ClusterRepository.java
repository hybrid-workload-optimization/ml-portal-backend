package kr.co.strato.domain.cluster.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.ClusterEntity;

public interface ClusterRepository extends JpaRepository<ClusterEntity, Long> {

}
