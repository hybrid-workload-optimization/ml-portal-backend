package kr.co.strato.portal.workload.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.Job;
import kr.co.strato.adapter.k8s.deployment.service.DeploymentAdapterService;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import kr.co.strato.portal.workload.model.JobArgDto;
import kr.co.strato.portal.workload.model.JobDto;
import kr.co.strato.portal.workload.model.JobDtoMapper;
import lombok.extern.slf4j.Slf4j;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.job.service.JobDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.model.PageRequest;


@Slf4j
@Service
public class JobService {
	@Autowired
	JobDomainService jobDomainService;
	
	@Autowired
	JobAdapterService jobAdapterService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	JobRepository jobRepository;
	
	//목록
	public Page<JobDto> getList(PageRequest pageRequest, JobArgDto args){
		Page<JobEntity> entities=  jobRepository.getPageList(pageRequest.of(), args);
		List<JobDto> dtos = entities.getContent().stream().map(JobDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		Page<JobDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public JobDto get(Long idx){
		JobEntity entitiy = jobDomainService.getById(idx);
		JobDto dto = JobDtoMapper.INSTANCE.toDto(entitiy);
		return dto;
	}
	
	//생성
	public void create(JobArgDto jobArgDto){
		save(jobArgDto);
	}
	
	//수정
	public void update(JobArgDto jobArgDto){
		save(jobArgDto);
	}
	
	private void save(JobArgDto jobArgDto){
		Long clusterId = jobArgDto.getClusterId();
		String yaml = jobArgDto.getYaml();
		
		
		List<Job> jobs = jobAdapterService.create(clusterId, yaml);
		//deployment 저장.
		List<JobEntity> eneities = jobs.stream().map(j -> {
			JobEntity jobEntity = null;
			try {
				jobEntity = toEntity(j);
			} catch (JsonProcessingException e) {
				log.debug(yaml);
			}
			
			if(jobEntity != null) {
				NamespaceEntity namespaceEntity = new NamespaceEntity();
				namespaceEntity.setId(jobArgDto.getNamespaceIdx());
				jobEntity.setNamespaceEntity(namespaceEntity);
				
				//수정시
				if(jobArgDto.getJobIdx() != null)
					jobEntity.setJobIdx(jobArgDto.getJobIdx());
				
				jobDomainService.save(jobEntity);
			}
			
			return jobEntity;
		}).collect(Collectors.toList());
		
		//TODO replicatset 저장.
		//TODO pod 저장.
	}
	
	
	//삭제
	public void delete(JobArgDto jobArgDto){
		Long clusterId = jobArgDto.getClusterId();
		Long namespaceIdx = jobArgDto.getNamespaceIdx();
		Long deploymentIdx = jobArgDto.getJobIdx();
		String jobName = null;
		String namespaceName = null;
		
		NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceIdx);
		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();
		
		JobEntity jobEntity = jobDomainService.getById(deploymentIdx);
		if(jobEntity != null)
			jobName = jobEntity.getJobName();
		
		jobAdapterService.delete(clusterId, namespaceName, jobName);
		jobDomainService.delete(deploymentIdx);
	}
	
	
    private JobEntity toEntity(Job job) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        String name = null;
        String uid = null;
        String annotation = null;
        String label = null;
        String strategy = null;
        String selector = null;
        String maxSurge = null;
        String maxUnavailable = null;
        Integer podUpdated = null;
        Integer podReplicas = null;
        Integer podReady = null;
        String condition = null;
        String image = null;
        
        float fMaxSurge = 0f;
        if(maxSurge != null && !maxSurge.isEmpty())
        	fMaxSurge = Float.parseFloat(maxSurge);
        
        float fMaxUnavailable = 0f;
        if(maxUnavailable != null && !maxUnavailable.isEmpty())
        	fMaxUnavailable = Float.parseFloat(maxUnavailable);
        
        JobEntity deploymentEntity = JobEntity.builder()
				.jobName(name)
				.jobUid(uid)
				.image(image)
				.createdAt(LocalDateTime.now())
				.build();

        return deploymentEntity;
    }
}
