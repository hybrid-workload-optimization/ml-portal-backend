package kr.co.strato.domain.work.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.repository.WorkJobRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class WorkJobDomainService {

	@Autowired
	WorkJobRepository workJobRepository;
	
	
	public void register(WorkJobEntity workJobEntity) {
		workJobRepository.save(workJobEntity);
	}
	
	public void update(WorkJobEntity workJobEntity) {
		Optional<WorkJobEntity> workJob = workJobRepository.findById(workJobEntity.getWorkJobIdx());
		if (workJob.isPresent()) {
			workJobRepository.save(workJobEntity);
		} else {
			throw new NotFoundResourceException("work_job_idx : " + workJobEntity.getWorkJobIdx());
		}
	}
	
	public WorkJobEntity get(Long workJobIdx) {
		Optional<WorkJobEntity> workJobEntity = workJobRepository.findById(workJobIdx);
		if (workJobEntity.isPresent()) {
			return workJobEntity.get();
		}
		return null;
	}
	 
}
