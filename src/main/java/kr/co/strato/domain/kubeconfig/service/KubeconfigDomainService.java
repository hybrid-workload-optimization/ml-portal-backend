package kr.co.strato.domain.kubeconfig.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.kubeconfig.model.KubeconfigEntity;
import kr.co.strato.domain.kubeconfig.repository.KubeconfigRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class KubeconfigDomainService {
	
	@Autowired
	KubeconfigRepository kubeconfigRepository;
	
	public List<KubeconfigEntity> getAll(){
		return kubeconfigRepository.findAll();
	}
	
	public KubeconfigEntity getById(Long idx){
		Optional<KubeconfigEntity> job = kubeconfigRepository.findById(idx);
		if (job.isPresent())
			return job.get();
		else
			throw new NotFoundResourceException("job_idx : " + idx);
	}
	
	public KubeconfigEntity save(KubeconfigEntity jobEntity){
		KubeconfigEntity result = kubeconfigRepository.save(jobEntity);
		return result;
	}
	
	public void delete(Long idx){
		kubeconfigRepository.deleteById(idx);
	}
	
}
