package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobArgDto {
	private Long clusterIdx;
	private Long clusterId;
	private String yaml;
	
	private Long jobIdx;
	private String jobName;
	private Long namespaceIdx;
	private String namespaceName;
	private String uid;
}
