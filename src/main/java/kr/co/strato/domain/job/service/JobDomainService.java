package kr.co.strato.domain.job.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class JobDomainService {
	
	@Autowired
	JobRepository jobRepository;
	
	public List<JobEntity> getAll(){
		return jobRepository.findAll();
	}
	
	public JobEntity getById(Long idx){
		Optional<JobEntity> job = jobRepository.findById(idx);
		if (job.isPresent())
			return job.get();
		else
			throw new NotFoundResourceException("job_idx : " + idx);
	}
	
	public JobEntity save(JobEntity jobEntity){
		JobEntity result = jobRepository.save(jobEntity);
		return result;
	}
	
	public void delete(Long idx){
		jobRepository.deleteById(idx);
	}
	
	public Integer deleteByNamespaceEntity(NamespaceEntity namespace) {
		return jobRepository.deleteByNamespaceEntity(namespace);
	}
}
