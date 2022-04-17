package kr.co.strato.portal.workload.model;

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
	private String createdAt;
}
