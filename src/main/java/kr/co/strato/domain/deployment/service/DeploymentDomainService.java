package kr.co.strato.domain.deployment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;

@Service
public class DeploymentDomainService {
	
	@Autowired
	DeploymentRepository deploymentRepository;
	
	public List<DeploymentEntity> getDeploymentEntities(){
		return deploymentRepository.findAll();
	}
	
	public DeploymentEntity save(DeploymentEntity deploymentEntity){
		DeploymentEntity result = deploymentRepository.save(deploymentEntity);
		return result;
	}
	
	public void delete(Long idx){
		deploymentRepository.deleteById(idx);
	}
}
