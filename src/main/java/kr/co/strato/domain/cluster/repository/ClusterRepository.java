package kr.co.strato.domain.cluster.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.Cluster;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {

}
