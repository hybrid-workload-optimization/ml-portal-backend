package kr.co.strato.portal.workload.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronJobDto {
	private Long idx;
	private String name;
	private String uid;
	private String label;
	private String clusterName;
	private Long namespaceIdx;
	private String namespace;
	private String schedule;
	private String pause;
	private String lastSchedule;
	private String concurrencyPolicy;
	private String createdAt;
	private Integer active;
	private Long projectIdx;
	
	private List<JobDto> activeJobs;
	private List<JobDto> inactiveJobs;
	
}
