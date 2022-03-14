package kr.co.strato.portal.cluster.model;

import java.util.HashMap;

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
	
	private String providerVersion;
	
	private String status;
	
	
	@Getter
	@Setter
	public static class List {
		private Long clusterIdx;
		private String clusterName;
		private String description;
		private String status;
		private int nodeCount;
		private String provider;
		private String providerVersion;
		private String createdAt;
		private HashMap<String, Object> problem;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long clusterIdx;
		private String clusterName;
		private String provider;
		private String providerVersion;
		private String createdAt;
		// status
		private String status;
		private HashMap<String, Object> problem;
		// master/worker
		private float availablePercentMaster;
		private float availablePercentWorker;
		private int masterCount;
		private int workerCount;
		// move to monitoring service
		private String monitoringServiceUrl;
	}
}
