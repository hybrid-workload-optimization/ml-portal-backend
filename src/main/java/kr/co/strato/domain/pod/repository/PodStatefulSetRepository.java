package kr.co.strato.domain.pod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.PodStatefulSetEntity;

public interface PodStatefulSetRepository extends JpaRepository<PodStatefulSetEntity, Long>, CustomPodRepository {
}
