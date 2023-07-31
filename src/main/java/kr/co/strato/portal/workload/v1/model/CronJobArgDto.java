package kr.co.strato.portal.workload.v1.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronJobArgDto {
	private Long clusterIdx;
	private Long clusterId;
	private String yaml;
	
	private Long jobIdx;
	private String jobName;
	private Long namespaceIdx;
	private String namespaceName;
	private String uid;
}
