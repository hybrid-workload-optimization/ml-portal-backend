package kr.co.strato.domain.persistentVolumeClaim.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

public interface CustomPersistentVolumeClaimRepository {
	
	public Page<PersistentVolumeClaimEntity> getPersistentVolumeClaimList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
	
	public List<PersistentVolumeClaimEntity> findByPod(Long podId);
}
