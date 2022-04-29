package kr.co.strato.domain.cronjob.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.cronjob.repository.CronJobRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class CronJobDomainService implements InNamespaceDomainService {
	
	@Autowired
	CronJobRepository cronJobRepository;
	
	public List<CronJobEntity> getAll(){
		return cronJobRepository.findAll();
	}
	
	public CronJobEntity getById(Long idx){
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
	
	public Integer deleteByNamespaceEntity(NamespaceEntity namespace) {
		return cronJobRepository.deleteByNamespaceEntity(namespace);
	}

	@Override
	public boolean isDuplicateName(Long clusterIdx, String namespace, String name) {
		Object obj = cronJobRepository.getCronJob(clusterIdx, namespace, name);
		return obj != null;
	}
}
