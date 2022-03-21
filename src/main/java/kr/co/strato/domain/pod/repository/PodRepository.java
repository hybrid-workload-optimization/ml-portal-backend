package kr.co.strato.domain.pod.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.pod.model.PodEntity;

public interface PodRepository extends JpaRepository<PodEntity, Long>, CustomPodRepository {
	public void deleteByOwnerUidAndKind(String ownerUid, String kind);
}
