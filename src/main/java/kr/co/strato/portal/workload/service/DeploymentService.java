package kr.co.strato.portal.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;
import kr.co.strato.domain.deployment.service.DeploymentDomainService;
import kr.co.strato.global.model.PageRequest;

@Service
public class DeploymentService {
	@Autowired
	DeploymentDomainService deploymentDomainService;
	
	@Autowired
	DeploymentRepository deploymentRepository;
	
	//목록
	public Page<DeploymentDto> getList(PageRequest pageRequest, DeploymentArgDto args){
		Page<DeploymentEntity> entities=  deploymentRepository.getDeploymentPageList(pageRequest.of(), args);
		List<DeploymentDto> dtos = entities.getContent().stream().map(DeploymentDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		Page<DeploymentDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public DeploymentDto get(Long idx){
		DeploymentEntity entitiy = deploymentDomainService.getDeploymentEntitiy(idx);
		DeploymentDto dto = DeploymentDtoMapper.INSTANCE.toDto(entitiy);
		return dto;
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
