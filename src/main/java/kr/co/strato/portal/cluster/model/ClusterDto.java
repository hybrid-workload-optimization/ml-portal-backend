package kr.co.strato.portal.cluster.model;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;

import kr.co.strato.adapter.k8s.cluster.model.ClusterHealthAdapterDto;
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
		private String provisioningType;
		// k8s
		private String kubeConfig;
		// kubespray
		private String provisioningUser;
		private ArrayList<Node> nodes;
		private ArrayList<Node> originalNodes;
		private ArrayList<String> removeNodes;
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
		private Long clusterId;
		private String status;
		private int nodeCount;
		private String provider;
		private String providerVersion;
		private String createdAt;
		private ArrayList<String> problem;
		private String provisioningType;
		private String provisioningStatus;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Status {
		private Long clusterIdx;
		private String status;		
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
		private String description;
		// status
		private String status;
		private ArrayList<String> problem;
		// provisioning
		private String provisioningType;
		private String provisioningStatus;
		private String provisioningLog;
		// kubespray
		private String provisioningUser;
		private ArrayList<Node> nodes;	
		// work job
		private Long workJobIdx;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Summary {
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
