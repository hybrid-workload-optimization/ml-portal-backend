package kr.co.strato.domain.storageClass.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.storageClass.repository.StorageClassRepository;

@Service
public class StorageClassDomainService {

	@Autowired
	private StorageClassRepository storageClassRepository;
	
}
