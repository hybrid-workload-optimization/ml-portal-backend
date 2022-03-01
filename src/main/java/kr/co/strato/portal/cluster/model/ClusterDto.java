package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterDto {
	
	private String clusterName;
	
	private String provider;
	
	@NotNull(message = "kube config is required")
	private String kubeConfig;
	
	private String description;
	
	private Long clusterId;
	
	private NodeDto node;
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class NodeDto {
		
		private Long nodeIdx;
		private String nodeName;
		private String nodeUid;
		private String ip;
		private String status;
		private String k8sVersion;
		private float allocatedCpu;
		private float allocatedMemory;
		private LocalDateTime createdAt;
		private String podCidr;
		private String osImage;
		private String kernelVersion;
		private String architecture;
		private String kubeletVersion;
		private String kubeproxyVersion;
		private Long clusterIdx;
		private String annotation;
		private String label;
		private String condition;
		private String role;
		
	}
	
}
