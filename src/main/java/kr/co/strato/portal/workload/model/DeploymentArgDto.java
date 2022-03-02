package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeploymentArgDto {
	private Long deploymentIdx;
	private Long projectIdx;
	private Long clusterIdx;
	private Long namespaceIdx;
	
	private Long clusterId;
	private String ymal;
}
