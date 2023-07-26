package kr.co.strato.portal.workload.v2.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.v2.model.CronJobDto;
import kr.co.strato.portal.workload.v2.model.JobDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;

@Service
public class CronJobServiceV2 extends WorkloadCommonV2 {
	
	@Autowired
	private JobServiceV2 jobService;

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		CronJobDto dto = new CronJobDto();
		setMetadataInfo(data, dto);		
		
		CronJob cronJob = (CronJob) data;
		
		CronJobSpec spec = cronJob.getSpec();
		CronJobStatus status = cronJob.getStatus();
		
		String schedule = spec.getSchedule();
		String concurrencyPolicy = spec.getConcurrencyPolicy();
		String lastSchedule = status.getLastScheduleTime();
		boolean pause = spec.getSuspend();
		
		List<ObjectReference> activeList = cronJob.getStatus().getActive();
		Integer active = activeList.size();
		
		
		List<String> activeUids = activeList.stream().map(ref -> ref.getUid()).collect(toList());	
		List<JobDto> jobs = jobService.getJobByOwnerUid(clusterEntity.getClusterId(), dto.getUid());
		
		List<JobDto> activeJobs = new ArrayList<>();
		List<JobDto> inactiveJobs = new ArrayList<>();
		for(JobDto job: jobs) {
			String uid = job.getUid();	
			if(activeUids.contains(uid)) {
				activeJobs.add(job);
			} else {
				inactiveJobs.add(job);
			}
		}
		
		dto.setActive(active);
		dto.setSchedule(schedule);
		dto.setLastSchedule(DateUtil.convertDateTime(lastSchedule));
		dto.setConcurrencyPolicy(concurrencyPolicy);
		dto.setPause(Boolean.toString(pause));
		dto.setActiveJobs(activeJobs);
		dto.setInactiveJobs(inactiveJobs);
		return dto;
	}
}
