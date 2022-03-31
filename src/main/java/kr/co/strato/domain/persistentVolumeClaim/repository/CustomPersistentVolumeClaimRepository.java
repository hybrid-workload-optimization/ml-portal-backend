package kr.co.strato.domain.persistentVolumeClaim.repository;
import java.util.List;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

public interface CustomPersistentVolumeClaimRepository {
	
	public List<PersistentVolumeClaimEntity> findByPod(Long podId);
	
}
