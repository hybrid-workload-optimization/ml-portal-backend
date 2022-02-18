package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterNodeDto {
	private Long id;
	private String name;
	private String uid;
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
