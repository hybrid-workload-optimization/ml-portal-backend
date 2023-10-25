package kr.co.strato.portal.workload.v1.service;

import static java.util.stream.Collectors.toList;

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

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.adapter.k8s.pod.model.PodMapper;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.job.service.JobDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.ml.v1.service.MLServiceInterface;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.v1.model.JobArgDto;
import kr.co.strato.portal.workload.v1.model.JobDto;
import kr.co.strato.portal.workload.v1.model.JobDtoMapper;
import kr.co.strato.portal.workload.v1.model.PodDto;
import kr.co.strato.portal.workload.v1.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class JobService extends InNamespaceService implements MLServiceInterface {
	@Autowired
	JobDomainService jobDomainService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	JobAdapterService jobAdapterService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	PodAdapterService podAdapterService;
	
	@Autowired
	JobRepository jobRepository;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectAuthorityService projectAuthorityService;
	
	//목록
	public Page<JobDto> getList(PageRequest pageRequest, JobArgDto args){
		Long clusterIdx = args.getClusterIdx();
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Page<JobEntity> entities=  jobRepository.getPageList(pageRequest.of(), args);
		List<JobDto> dtos = entities.getContent().stream().map(JobDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		dtos.forEach(j -> j.setClusterName(clusterEntity.getClusterName()));
		
		Page<JobDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public JobDto get(Long idx, UserDto loginUser){
		JobEntity entitiy = jobDomainService.getById(idx);
		JobDto dto = JobDtoMapper.INSTANCE.toDto(entitiy);
		
		
		NamespaceEntity namespace = entitiy.getNamespaceEntity();
		Long kubeConfigId = namespace.getCluster().getClusterId();
		
		Long clusterIdx = namespace.getCluster().getClusterIdx();
		Long clusterId = namespace.getCluster().getClusterId();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
		
		
		Job job = jobAdapterService.retrieve(kubeConfigId, namespace.getName(), entitiy.getJobName());
		if(job != null) {
			
			PodSpec podSpec = job.getSpec().getTemplate().getSpec();			
			Integer completions = job.getSpec().getCompletions();
			Integer parallelism = job.getSpec().getParallelism();
			
			Integer succeeded = job.getStatus().getSucceeded();
			Integer active = job.getStatus().getActive() == null ? 0: job.getStatus().getActive();
			
			List<String> images = podSpec.getContainers().stream().map(container -> container.getImage()).collect(toList());
			
			dto.setCompleted(completions);
			dto.setParallel(parallelism);
			dto.setImage(images.get(0));
			dto.setActive(active);
			dto.setSucceeded(succeeded);
			dto.setClusterId(clusterId);
	        
	        List<Pod> pods = podAdapterService.getList(kubeConfigId, null, entitiy.getJobUid(), null, null);
	        List<PodEntity> podEntitys = pods.stream().map(e -> PodMapper.INSTANCE.toEntity(e)).collect(Collectors.toList());
	        List<PodDto.ResListDto> dtos = podEntitys.stream().map(e -> PodDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
	        
	        dto.setPods(dtos);
	        
		}
		dto.setProjectIdx(projectIdx);
		return dto;
	}
	
	//yaml 조회
	public String getYaml(Long idx){		
		String jobName = null;
		String namespaceName = null;
		Long clusterId = null;
		
		String yaml = null;
		JobEntity entitiy = jobDomainService.getById(idx);
		if(entitiy != null) {
			yaml = entitiy.getYaml();
			
			jobName = entitiy.getJobName();
			NamespaceEntity namespaceEntity = entitiy.getNamespaceEntity();
			if(namespaceEntity != null) {
				namespaceName = namespaceEntity.getName();
				
				ClusterEntity cluster = namespaceEntity.getCluster();
				if(cluster != null)
					clusterId = cluster.getClusterId(); 
			}
		}
		
		if(yaml == null) {
			yaml = jobAdapterService.getYaml(clusterId, namespaceName, jobName);
		}
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
	
	private Long save(JobArgDto jobArgDto){
		Long clusterIdx = jobArgDto.getClusterIdx();
		String yaml = jobArgDto.getYaml();
		Long jobIdx = jobArgDto.getJobIdx();
		ClusterEntity clusterEntity = null;
		
		if(jobIdx == null) {
			//생성 시 이름 중복 채크
			//duplicateCheckResourceCreation(clusterIdx, yaml);			
			clusterEntity = clusterDomainService.get(clusterIdx);
			
		} else {
			JobEntity entity = jobDomainService.getById(jobIdx);
			clusterEntity = entity.getNamespaceEntity().getCluster();
		}
		
		
		List<Job> jobs = jobAdapterService.create(clusterEntity.getClusterId(), yaml);

		//job 저장.
		for(Job job : jobs) {
			JobEntity jobEntity = null;
			try {
				jobEntity = toEntity(job);
			} catch (JsonProcessingException ex) {
				log.debug(yaml);
			}
			
			if(jobEntity != null) {
				List<NamespaceEntity> namespaceEntities = namespaceDomainService.findByNameAndClusterIdx(job.getMetadata().getNamespace(), clusterEntity);
				if(namespaceEntities != null && namespaceEntities.size() > 0){
					jobEntity.setNamespaceEntity(namespaceEntities.get(0));
				}
				
				//수정시
				if(jobIdx!= null)
					jobEntity.setJobIdx(jobIdx);
				
				jobEntity.setYaml(Base64Util.decode(yaml));
				JobEntity entity = jobDomainService.save(jobEntity);
				return entity.getJobIdx();
			}
		}
		return -1L;
	}
	
	
	//삭제
	public void delete(JobArgDto jobArgDto){
		Long jobIdx = jobArgDto.getJobIdx();
		JobEntity jobEntity = jobDomainService.getById(jobIdx);
		NamespaceEntity namespaceEntity = jobEntity.getNamespaceEntity();
		ClusterEntity clusterEntity = namespaceEntity.getCluster();
		
		String namespaceName = null;
		Long clusterId = null;
		String jobName = null;

		if(clusterEntity != null)
			clusterId = clusterEntity.getClusterId();

		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();

		if(jobEntity != null)
			jobName = jobEntity.getJobName();
		
		
		jobAdapterService.delete(clusterId, namespaceName, jobName);
		jobDomainService.delete(jobIdx);
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
		
		String jobStatus	= mapper.writeValueAsString(podStatus);
		
		List<String> images = podSpec.getContainers().stream().map(container -> container.getImage()).collect(toList());
        
        
        JobEntity deploymentEntity = JobEntity.builder()
				.jobName(name)
				.jobUid(uid)
				.annotation(annotations)
				.label(label)
				.status(jobStatus)
				.image(images.get(0))
				.parallelExecution(Integer.toString(parallelism))
				.completionMode(Integer.toString(completions))
				.createdAt(DateUtil.convertDateTime(createAt))
				.build();

        return deploymentEntity;
    }

	@Override
	protected InNamespaceDomainService getDomainService() {
		return jobDomainService;
	}

	@Override
	public Long mlResourceApply(Long clusterIdx, Long resourceId, String yaml) {
		JobArgDto jobDto = new JobArgDto();
		jobDto.setClusterIdx(clusterIdx);
		jobDto.setYaml(Base64Util.encode(yaml));
		jobDto.setJobIdx(resourceId);
		return save(jobDto);
	}
	
	@Override
	public boolean delete(Long resourceId, String yaml) {
		JobArgDto jobArgDto = new JobArgDto();
		jobArgDto.setJobIdx(resourceId);
		
		try {
			delete(jobArgDto);
		} catch (Exception e) {
			log.error("", e);
		}
		
		return true;
	}
	
	@Override
	public Object getEntity(Long resourceId) {
		JobEntity entitiy = jobDomainService.getById(resourceId);
		return entitiy;
	}
	
	@Override
	public String getResourceUid(Long resourceId) {
		JobEntity entitiy = jobDomainService.getById(resourceId);
		return entitiy.getJobUid();
	}
	
	@Override
	public HasMetadata getResource(Long resourceId) {
		JobEntity entitiy = jobDomainService.getById(resourceId);
		Long kubeConfigId = entitiy.getNamespaceEntity().getCluster().getClusterId();
		String namespace = entitiy.getNamespaceEntity().getName();
		String name = entitiy.getJobName();
		
		return jobAdapterService.retrieve(kubeConfigId, namespace, name);
	}
}
