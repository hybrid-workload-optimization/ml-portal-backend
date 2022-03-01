package kr.co.strato.domain.deployment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class DeploymentDomainService {
	
	@Autowired
	DeploymentRepository deploymentRepository;
	
	public List<DeploymentEntity> getDeploymentEntities(){
		return deploymentRepository.findAll();
	}
	
	public DeploymentEntity getDeploymentEntitiy(Long idx){
		Optional<DeploymentEntity> deployment = deploymentRepository.findById(idx);
		if (deployment.isPresent())
			return deployment.get();
		else
			throw new NotFoundResourceException("deployment_idx : " + idx);
	}
	
	public DeploymentEntity save(DeploymentEntity deploymentEntity){
		DeploymentEntity result = deploymentRepository.save(deploymentEntity);
		return result;
	}
	
	public void delete(Long idx){
		deploymentRepository.deleteById(idx);
	}
}
