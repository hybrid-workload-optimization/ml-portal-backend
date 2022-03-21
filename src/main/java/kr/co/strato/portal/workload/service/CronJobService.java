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

import io.fabric8.kubernetes.api.model.batch.CronJob;
import io.fabric8.kubernetes.api.model.batch.Job;
import kr.co.strato.adapter.k8s.cronjob.service.CronJobAdapterService;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.portal.workload.model.CronJobArgDto;
import kr.co.strato.portal.workload.model.CronJobDto;
import kr.co.strato.portal.workload.model.CronJobDtoMapper;
import lombok.extern.slf4j.Slf4j;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.cronjob.repository.CronJobRepository;
import kr.co.strato.domain.cronjob.service.CronJobDomainService;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.job.service.JobDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;


@Slf4j
@Service
public class CronJobService {
	@Autowired
	CronJobDomainService cronJobDomainService;
	
	@Autowired
	CronJobAdapterService cronJobAdapterService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	CronJobRepository cronJobRepository;
	
	//목록
	public Page<CronJobDto> getList(PageRequest pageRequest, CronJobArgDto args){
		Page<CronJobEntity> entities=  cronJobRepository.getPageList(pageRequest.of(), args);
		List<CronJobDto> dtos = entities.getContent().stream().map(CronJobDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
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
	public void create(CronJobArgDto CronJobArgDto){
		save(CronJobArgDto);
	}
	
	//수정
	public void update(CronJobArgDto CronJobArgDto){
		save(CronJobArgDto);
	}
	
	private void save(CronJobArgDto CronJobArgDto){
		Long clusterId = CronJobArgDto.getClusterId();
		String yaml = CronJobArgDto.getYaml();
		
		List<CronJob> jobs = cronJobAdapterService.create(clusterId, yaml);
		//job 저장.
		List<CronJobEntity> eneities = jobs.stream().map(e -> {
			CronJobEntity CronJobEntity = null;
			try {
				CronJobEntity = toEntity(e);
			} catch (JsonProcessingException ex) {
				log.debug(yaml);
			}
			
			if(CronJobEntity != null) {
				NamespaceEntity namespaceEntity = new NamespaceEntity();
				namespaceEntity.setId(CronJobArgDto.getNamespaceIdx());
				CronJobEntity.setNamespaceEntity(namespaceEntity);
				
				//수정시
				if(CronJobArgDto.getJobIdx() != null)
					CronJobEntity.setCronJobIdx(CronJobArgDto.getJobIdx());
				
				cronJobDomainService.save(CronJobEntity);
			}
			
			return CronJobEntity;
		}).collect(Collectors.toList());
		
		//TODO replicatset 저장.
		//TODO pod 저장.
	}
	
	
	//삭제
	public void delete(CronJobArgDto CronJobArgDto){
		Long clusterId = CronJobArgDto.getClusterId();
		Long namespaceIdx = CronJobArgDto.getNamespaceIdx();
		Long deploymentIdx = CronJobArgDto.getJobIdx();
		String jobName = null;
		String namespaceName = null;
		
		NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceIdx);
		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();
		
		CronJobEntity cronJobEntity = cronJobDomainService.getById(deploymentIdx);
		if(cronJobEntity != null)
			jobName = cronJobEntity.getCronJobName();
		
		cronJobAdapterService.delete(clusterId, namespaceName, jobName);
		cronJobDomainService.delete(deploymentIdx);
	}
	
	
    private CronJobEntity toEntity(CronJob job) throws JsonProcessingException {
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
        
        CronJobEntity entity = CronJobEntity.builder()
				.cronJobName(name)
				.cronJobUid(uid)
				.createdAt(LocalDateTime.now())
				.build();

        return entity;
    }
}
