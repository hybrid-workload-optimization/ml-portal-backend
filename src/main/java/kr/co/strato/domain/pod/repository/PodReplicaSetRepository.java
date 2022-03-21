package kr.co.strato.domain.pod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.pod.model.PodReplicaSetEntity;

public interface PodReplicaSetRepository extends JpaRepository<PodReplicaSetEntity, Long>, CustomPodRepository {
}
