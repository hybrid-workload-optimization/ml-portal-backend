package kr.co.strato.domain.persistentVolumeClaim.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.repository.CustomPersistentVolumeClaimRepository;
import kr.co.strato.domain.persistentVolumeClaim.repository.PersistentVolumeClaimRepository;

@Service
public class PersistentVolumeClaimDomainService {

	@Autowired
	private PersistentVolumeClaimRepository persistentVolumeClaimRepository;
	
	
	public List<PersistentVolumeClaimEntity> getPodPersistentVolumeClaimList(Long podId) {
		List<PersistentVolumeClaimEntity> persistentVolumeClaimList = persistentVolumeClaimRepository.findByPod(podId);
		return persistentVolumeClaimList;
    }
}
