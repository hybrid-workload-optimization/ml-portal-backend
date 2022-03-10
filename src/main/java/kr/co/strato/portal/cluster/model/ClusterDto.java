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
	
	private Long clusterIdx;
	
	private String clusterName;
	
	private String provider;
	
	@NotNull(message = "kube config is required")
	private String kubeConfig;
	
	private String description;
	
	private Long clusterId;
	
}
