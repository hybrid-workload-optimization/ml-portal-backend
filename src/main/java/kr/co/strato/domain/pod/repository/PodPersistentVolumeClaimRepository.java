package kr.co.strato.domain.pod.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimPK;

public interface PodPersistentVolumeClaimRepository extends JpaRepository<PodPersistentVolumeClaimEntity, PodPersistentVolumeClaimPK>, CustomPodRepository {
	public List<PodPersistentVolumeClaimEntity> findByPod(PodEntity pod);
};
