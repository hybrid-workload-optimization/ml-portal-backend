package kr.co.strato.domain.persistentVolumeClaim.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.persistentVolumeClaim.repository.PersistentVolumeClaimRepository;

@Service
public class PersistentVolumeClaimDomainService {

	@Autowired
	private PersistentVolumeClaimRepository persistentVolumeClaimRepository;
	
}
