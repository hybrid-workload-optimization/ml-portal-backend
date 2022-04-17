package kr.co.strato.portal.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.batch.CronJob;
import io.fabric8.kubernetes.api.model.batch.CronJobSpec;
import io.fabric8.kubernetes.api.model.batch.CronJobStatus;
import io.fabric8.kubernetes.api.model.batch.Job;
import kr.co.strato.adapter.k8s.cronjob.service.CronJobAdapterService;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.cronjob.repository.CronJobRepository;
import kr.co.strato.domain.cronjob.service.CronJobDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.service.JobDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.CronJobArgDto;
import kr.co.strato.portal.workload.model.CronJobDto;
import kr.co.strato.portal.workload.model.CronJobDtoMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CronJobService {
	@Autowired
	CronJobDomainService cronJobDomainService;
	
	@Autowired
	JobService jobService;
	
	@Autowired
	JobDomainService jobDomainService;
	
	@Autowired
	JobAdapterService jobAdapterService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	CronJobAdapterService cronJobAdapterService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	CronJobRepository cronJobRepository;
	
	//목록
	public Page<CronJobDto> getList(PageRequest pageRequest, CronJobArgDto args) {		
		Page<CronJobEntity> entities=  cronJobRepository.getPageList(pageRequest.of(), args);
		List<CronJobDto> dtos = entities.getContent().stream().map(CronJobDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		
		//TODO 클러스터 이름 적용 부분 수정
		Long clusterIdx = args.getClusterIdx();
		if(clusterIdx != null) {
			ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
			dtos.forEach(j -> j.setClusterName(clusterEntity.getClusterName()));
		}
		Page<CronJobDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public CronJobDto get(Long idx){
		CronJobEntity entitiy = cronJobDomainService.getById(idx);
		CronJobDto dto = CronJobDtoMapper.INSTANCE.toDto(entitiy);
		return dto;
	}
	
	//yaml 조회
	public String getYaml(Long idx){
		
		String name = null;
		String namespaceName = null;
		Long clusterId = null;
		
		
		CronJobEntity entitiy = cronJobDomainService.getById(idx);
		if(entitiy != null) {
			name = entitiy.getCronJobName();
			NamespaceEntity namespaceEntity = entitiy.getNamespaceEntity();
			if(namespaceEntity != null) {
				namespaceName = namespaceEntity.getName();
				
				ClusterEntity cluster = namespaceEntity.getCluster();
				if(cluster != null)
					clusterId = cluster.getClusterId(); 
			}
		}
		String yaml = cronJobAdapterService.getYaml(clusterId, namespaceName, name);
		if(yaml != null)
			yaml = Base64Util.encode(yaml);
		return yaml;
	}
	
	//생성
	public void create(CronJobArgDto cronJobArgDto){
		save(cronJobArgDto);
	}
	
	//수정
	public void update(CronJobArgDto cronJobArgDto){
		save(cronJobArgDto);
	}
	
	private void save(CronJobArgDto cronJobArgDto){
		Long clusterIdx = cronJobArgDto.getClusterIdx();
		String yaml = cronJobArgDto.getYaml();
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long clusterId = clusterEntity.getClusterId();
		
		List<CronJob> jobs = cronJobAdapterService.create(clusterId, yaml);
		
		
		//job 저장.
		List<CronJobEntity> eneities = jobs.stream().map(e -> {
			CronJobEntity cronJobEntity = null;
			try {
				cronJobEntity = toEntity(clusterEntity, e);
			} catch (JsonProcessingException ex) {
				log.debug(yaml);
			}
			
			if(cronJobEntity != null) {
				List<NamespaceEntity> namespaceEntities = namespaceDomainService.findByNameAndClusterIdx(e.getMetadata().getNamespace(), clusterEntity);
				if(namespaceEntities != null && namespaceEntities.size() > 0){
					cronJobEntity.setNamespaceEntity(namespaceEntities.get(0));
				}
				
				//수정시
				if(cronJobArgDto.getJobIdx() != null)
					cronJobEntity.setCronJobIdx(cronJobArgDto.getJobIdx());
				
				try {
					List<Job> jobsList = jobAdapterService.getListFromOwnerUid(clusterEntity.getClusterId(), cronJobEntity.getCronJobUid());
					for(Job j: jobsList) {
						JobEntity job = jobService.toEntity(j);
						//수정 시에는 기존 잡 목록 삭제
						if(cronJobArgDto.getJobIdx() != null) {
							jobDomainService.deleteByCronJobIdx(cronJobArgDto.getJobIdx());
						}
						
						job.setCronJobIdx(cronJobEntity.getCronJobIdx());
						jobDomainService.save(job);
						cronJobEntity.setJobEntity(job);
					}				
					
				} catch (Exception e1) {
					log.error("크론잡 저장 실패", e);
				}
				
				cronJobDomainService.save(cronJobEntity);
			}
			
			return cronJobEntity;
		}).collect(Collectors.toList());
		
		//TODO 이어지는 작업 확인
	}
	
	
	//삭제
	public void delete(CronJobArgDto CronJobArgDto){
		Long cronJobIdx = CronJobArgDto.getJobIdx();
		CronJobEntity cronJobEntity = cronJobDomainService.getById(cronJobIdx);
		NamespaceEntity namespaceEntity = cronJobEntity.getNamespaceEntity();
		ClusterEntity clusterEntity = namespaceEntity.getCluster();
		
		String namespaceName = null;
		Long clusterId = null;
		String cronJobName = null;

		if(clusterEntity != null)
			clusterId = clusterEntity.getClusterId();

		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();

		if(cronJobEntity != null)
			cronJobName = cronJobEntity.getCronJobName();
		
		cronJobAdapterService.delete(clusterId, namespaceName, cronJobName);
		cronJobDomainService.delete(cronJobIdx);
	}
	
	
    private CronJobEntity toEntity(ClusterEntity clusterEntity, CronJob job) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        String name 		= job.getMetadata().getName();
        String uid			= job.getMetadata().getUid();
        String label		= mapper.writeValueAsString(job.getMetadata().getLabels());
        String createAt		= job.getMetadata().getCreationTimestamp();
        String annotations	= mapper.writeValueAsString(job.getMetadata().getAnnotations());
        
        
        CronJobSpec spec = job.getSpec();
		CronJobStatus status = job.getStatus();
		
		String schedule = spec.getSchedule();
		String concurrencyPolicy = spec.getConcurrencyPolicy();
		String lastSchedule = status.getLastScheduleTime();
		boolean pause = spec.getSuspend();
        
        CronJobEntity entity = CronJobEntity.builder()
				.cronJobName(name)
				.cronJobUid(uid)
				.schedule(schedule)
				.concurrencyPolicy(concurrencyPolicy)
				.lastSchedule(DateUtil.convertDateTime(lastSchedule))
				.pause(Boolean.toString(pause))
				.label(label)
				.annotation(annotations)
				.createdAt(DateUtil.convertDateTime(createAt))
				.build();

        return entity;
    }
}
