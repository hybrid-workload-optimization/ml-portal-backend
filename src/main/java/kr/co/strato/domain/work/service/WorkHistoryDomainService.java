package kr.co.strato.domain.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.work.model.WorkHistoryEntity;
import kr.co.strato.domain.work.repository.WorkHistoryRepository;

@Service
public class WorkHistoryDomainService {

	@Autowired
	WorkHistoryRepository workHistoryRepository;
	
	
	public void register(WorkHistoryEntity workHistoryEntity) {
		workHistoryRepository.save(workHistoryEntity);
	}
}
