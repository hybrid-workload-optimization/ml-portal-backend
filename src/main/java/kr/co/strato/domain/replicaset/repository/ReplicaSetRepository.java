package kr.co.strato.domain.replicaset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

public interface ReplicaSetRepository extends JpaRepository<ReplicaSetEntity, Long>, CustomReplicaSetRepository {

}
