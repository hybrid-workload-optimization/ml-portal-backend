package kr.co.strato.portal.workload.service;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobSpec;
import io.fabric8.kubernetes.api.model.batch.JobStatus;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.job.service.JobDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.JobArgDto;
import kr.co.strato.portal.workload.model.JobDto;
import kr.co.strato.portal.workload.model.JobDtoMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class JobService {
	@Autowired
	JobDomainService jobDomainService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
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
	
	//yaml 조회
	public String getYaml(Long idx){
		
		String jobName = null;
		String namespaceName = null;
		Long clusterId = null;
		
		
		JobEntity entitiy = jobDomainService.getById(idx);
		if(entitiy != null) {
			jobName = entitiy.getJobName();
			NamespaceEntity namespaceEntity = entitiy.getNamespaceEntity();
			if(namespaceEntity != null) {
				namespaceName = namespaceEntity.getName();
				
				ClusterEntity cluster = namespaceEntity.getCluster();
				if(cluster != null)
					clusterId = cluster.getClusterId(); 
			}
		}
		String yaml = jobAdapterService.getYaml(clusterId, namespaceName, jobName);
		if(yaml != null)
			yaml = Base64Util.encode(yaml);
		return yaml;
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
		Long clusterIdx = jobArgDto.getClusterIdx();
		String yaml = jobArgDto.getYaml();
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long clusterId = clusterEntity.getClusterId();
		List<Job> jobs = jobAdapterService.create(clusterId, yaml);
		//job 저장.
		List<JobEntity> eneities = jobs.stream().map(e -> {
			JobEntity jobEntity = null;
			try {
				jobEntity = toEntity(e);
			} catch (JsonProcessingException ex) {
				log.debug(yaml);
			}
			
			if(jobEntity != null) {
				List<NamespaceEntity> namespaceEntities = namespaceDomainService.findByNameAndClusterIdx(e.getMetadata().getNamespace(), clusterEntity);
				if(namespaceEntities != null && namespaceEntities.size() > 0){
					jobEntity.setNamespaceEntity(namespaceEntities.get(0));
				}
				
				//수정시
				if(jobArgDto.getJobIdx() != null)
					jobEntity.setJobIdx(jobArgDto.getJobIdx());
				
				jobDomainService.save(jobEntity);
			}
			
			return jobEntity;
		}).collect(Collectors.toList());
		
		//TODO 이어지는 작업 확인
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
	
	
    public JobEntity toEntity(Job job) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        String name 		= job.getMetadata().getName();
        String uid			= job.getMetadata().getUid();
        String label		= mapper.writeValueAsString(job.getMetadata().getLabels());
        String createAt		= job.getMetadata().getCreationTimestamp();
        String annotations	= mapper.writeValueAsString(job.getMetadata().getAnnotations());
       
        JobSpec spec = job.getSpec();
		JobStatus status = job.getStatus();
		ObjectMeta meta = job.getMetadata();
		
		Integer replicas = status.getActive();
		Integer succeeded = status.getSucceeded();
		Integer active = status.getActive() == null ? 0: status.getActive();
		
		Integer completions = spec.getCompletions();
		Integer parallelism = spec.getParallelism();
		
		
		PodSpec podSpec = spec.getTemplate().getSpec();	
		
		
		
		Map<String, Object> podStatus = new HashMap<>();
		podStatus.put("Succeeded", succeeded);
		podStatus.put("Desired", succeeded);
		
		List<String> images = podSpec.getContainers().stream().map(container -> container.getImage()).collect(toList());
        
        
        JobEntity deploymentEntity = JobEntity.builder()
				.jobName(name)
				.jobUid(uid)
				.annotation(annotations)
				.label(label)
				.status(annotations)
				.image(images.get(0))
				.parallelExecution(Integer.toString(parallelism))
				.completionMode(Integer.toString(completions))
				.createdAt(DateUtil.convertDateTime(createAt))
				.build();

        return deploymentEntity;
    }
}
