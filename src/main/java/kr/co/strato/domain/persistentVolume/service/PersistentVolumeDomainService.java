package kr.co.strato.domain.persistentVolume.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.persistentVolume.repository.PersistentVolumeRepository;

@Service
public class PersistentVolumeDomainService {

	@Autowired
	private PersistentVolumeRepository persistentVolumeRepository;
	
}
