package kr.co.strato.domain.persistentVolumeClaim.service;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.repository.CustomPersistentVolumeClaimRepository;
import kr.co.strato.domain.persistentVolumeClaim.repository.PersistentVolumeClaimRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class PersistentVolumeClaimDomainService {

	@Autowired
	private PersistentVolumeClaimRepository persistentVolumeClaimRepository;
	
	
	public Long register(PersistentVolumeClaimEntity persistentVolumeClaimEntity) {
		persistentVolumeClaimRepository.save(persistentVolumeClaimEntity);
		
		return persistentVolumeClaimEntity.getId();
	}
	
	public Page<PersistentVolumeClaimEntity> getList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		return persistentVolumeClaimRepository.getPersistentVolumeClaimList(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
	
	public List<PersistentVolumeClaimEntity> getPodPersistentVolumeClaimList(Long podId) {
		List<PersistentVolumeClaimEntity> persistentVolumeClaimList = persistentVolumeClaimRepository.findByPod(podId);
		return persistentVolumeClaimList;
    }
	
	public PersistentVolumeClaimEntity get(Long persistentVolumeClaimIdx) {
		Optional<PersistentVolumeClaimEntity> persistentVolumeClaim = persistentVolumeClaimRepository.findById(persistentVolumeClaimIdx);
		if (persistentVolumeClaim.isPresent()) {
			return persistentVolumeClaim.get();
		} else {
			throw new NotFoundResourceException("Persistent Volume Claim : " + persistentVolumeClaim.toString());
		}
	}
	
	public void delete(PersistentVolumeClaimEntity persistentVolumeClaimEntity) {
		persistentVolumeClaimRepository.delete(persistentVolumeClaimEntity);
	}
}
