package kr.co.strato.portal.workload.v2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import kr.co.strato.adapter.k8s.job.service.JobAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.workload.v2.model.JobDto;
import kr.co.strato.portal.workload.v2.model.PodDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private PodServiceV2 podService;
	
	@Autowired
	JobAdapterService jobAdapterService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {	    
		return toDto(clusterEntity.getClusterId(), data);
	}
	
	public JobDto toDto(Long kubeConfigId, HasMetadata data) {
		JobDto dto = new JobDto();
		setMetadataInfo(data, dto);		
		
		Job job = (Job) data;
		
		// TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
		String image = job.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();	
		Integer completions = job.getSpec().getCompletions();
		Integer parallelism = job.getSpec().getParallelism();
		
		Integer succeeded = job.getStatus().getSucceeded();
		Integer active = job.getStatus().getActive() == null ? 0: job.getStatus().getActive();
		
		List<PodDto> pods = null;
		try {
			pods = podService.getPodByOwnerUid(kubeConfigId, dto.getUid());
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		dto.setCompleted(completions);
		dto.setParallel(parallelism);
		dto.setImage(image);
		dto.setActive(active);
		dto.setSucceeded(succeeded);
	    dto.setPods(pods);
	    dto.setPod(active + "/" + succeeded);
	    
		return dto;
	}
	
	/**
	 * ownerUid를 이용하여 Job 리스트를 조회한다.
	 * @param kubeConfigId
	 * @param ownerUid
	 * @return
	 * @throws Exception
	 */
	public List<JobDto> getJobByOwnerUid(Long kubeConfigId, String ownerUid) throws Exception  {
		List<Job> list = jobAdapterService.getListFromOwnerUid(kubeConfigId, ownerUid);
		if(list != null && list.size() > 0) {
			List<JobDto> newList = list.stream().map(j -> toDto(kubeConfigId, j)).collect(Collectors.toList());
			return newList;
		}
		return null;
	}
}
