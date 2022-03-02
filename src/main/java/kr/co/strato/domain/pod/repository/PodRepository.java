package kr.co.strato.domain.pod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface PodRepository extends JpaRepository<StatefulSetEntity, Long>, CustomPodRepository {
}
