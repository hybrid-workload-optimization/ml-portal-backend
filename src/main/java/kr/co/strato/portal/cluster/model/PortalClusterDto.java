package kr.co.strato.portal.cluster.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PortalClusterDto {
	
	@NotNull(message = "cluster name is required")
	private String clusterName;
	
	@NotNull(message = "provider is required")
	private String provider;
	
	@NotNull(message = "kube config is required")
	private String kubeConfig;
	
	private String description;
	
}
