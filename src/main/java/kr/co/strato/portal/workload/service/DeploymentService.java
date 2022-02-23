package kr.co.strato.portal.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.service.DeploymentDomainService;

@Service
public class DeploymentService {
	@Autowired
	DeploymentDomainService deploymentDomainService;
	
	
	//목록
	public List<DeploymentDto> getList(){
		List<DeploymentEntity> entities = deploymentDomainService.getDeploymentEntities();
		List<DeploymentDto> dtos = entities.stream().map(DeploymentDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		return dtos;
	}
	
	
	//상세
	public DeploymentDto get(Long idx){
		return new DeploymentDto();
	}
	
	//저장/수정
	public void save(DeploymentDto deploymentDto){
		DeploymentEntity deploymentEntity = DeploymentDtoMapper.INSTANCE.toEntity(deploymentDto);
		deploymentDomainService.save(deploymentEntity);
	}
	
	
	//삭제
	public void delete(Long idx){
		deploymentDomainService.delete(idx);
	}
}
