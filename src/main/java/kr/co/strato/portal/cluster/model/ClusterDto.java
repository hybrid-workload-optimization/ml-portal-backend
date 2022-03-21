package kr.co.strato.portal.cluster.model;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ClusterDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class Form {
		private Long clusterIdx;
		@NotEmpty(message = "cluster name is required")
		private String clusterName;
		@NotEmpty(message = "provider is required")
		private String provider;
		private String description;
		private Long clusterId;
		// k8s
		private String kubeConfig;
		// kubespray
		private ArrayList<Node> nodes;	
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class Node {
		private String name;
		private String ip;
		private ArrayList<String> nodeTypes;	
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class List {
		private Long clusterIdx;
		private String clusterName;
		private String description;
		private String status;
		private int nodeCount;
		private String provider;
		private String providerVersion;
		private String createdAt;
		private ArrayList<String> problem;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Detail {
		private Long clusterIdx;
		private Long clusterId;
		private String clusterName;
		private String provider;
		private String providerVersion;
		private String createdAt;
		private String kubeConfig;
		private String description;
		
		// status
		private String status;
		private ArrayList<String> problem;
		
		// master/worker
		private int masterCount;
		private int workerCount;
		private float availableMasterPercent;
		private float availableWorkerPercent;
		
		private int namespaceCount;
		private int podCount;
		private int pvcCount;
		
		private String monitoringServiceUrl;
	}
}
