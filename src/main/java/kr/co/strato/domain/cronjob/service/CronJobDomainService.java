package kr.co.strato.domain.cronjob.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.cronjob.repository.CronJobRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class CronJobDomainService {
	
	@Autowired
	CronJobRepository cronJobRepository;
	
	public List<CronJobEntity> getAll(){
		return cronJobRepository.findAll();
	}
	
	public CronJobEntity getByIdx(Long idx){
		Optional<CronJobEntity> job = cronJobRepository.findById(idx);
		if (job.isPresent())
			return job.get();
		else
			throw new NotFoundResourceException("job_idx : " + idx);
	}
	
	public CronJobEntity save(CronJobEntity CronJobEntity){
		CronJobEntity result = cronJobRepository.save(CronJobEntity);
		return result;
	}
	
	public void delete(Long idx){
		cronJobRepository.deleteById(idx);
	}
}
