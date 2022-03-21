package kr.co.strato.domain.pod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.pod.model.PodJobEntity;

public interface PodJobRepository extends JpaRepository<PodJobEntity, Long>, CustomPodRepository {
}
