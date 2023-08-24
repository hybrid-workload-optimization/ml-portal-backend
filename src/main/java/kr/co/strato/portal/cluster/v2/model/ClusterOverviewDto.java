package kr.co.strato.portal.cluster.v2.model;

import java.util.List;
import java.util.Map;

import kr.co.strato.portal.workload.v2.model.WorkloadDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterOverviewDto {

	@Getter
	@Setter	
	@Builder
	public static class Overview {
		private ClusterSummary clusterSummary;
		private List<WorkloadDto.ListDto> controlPlaneComponent;
		private List<NodeDto.ListDto> nodes;
		private List<NamespaceDto.ListDto> namespaces;
		private WorkloadSummary workloadSummary;
		private PodSummary podSummary;
	}
	
	@Getter
	@Setter
	@Builder
	public static class ClusterSummary {
		private String name;
		private String description;
		private String provider;
		private String region;
		private String vision;
		private String status;
		private String createAt;
		private String createBy;
		
		private String vpcCidr;
		private String serviceCidr;
		private String podCidr;
		
		private double cpuTotal;
		private double memoryTotal;
		private double cpuUsage;
		private double memoryUsage;
		private double storageTotal;
		private double storageUsage;
		
		private Integer countNode;
		private Integer countNamespace;
		private Integer countPV;
		private Integer countWorkload;
		private Integer countPod;
		private ClusterDto.Status healthy;
	}
	
	@Getter
	@Setter
	@Builder
	public static class WorkloadSummary {
		private List<WorkloadDto.ListDto> deployments;
		private List<WorkloadDto.ListDto> statefulSets;
		private List<WorkloadDto.ListDto> cronJobs;
		private List<WorkloadDto.ListDto> jobs;
		private List<WorkloadDto.ListDto> replicaSets;
		private List<WorkloadDto.ListDto> daemonSets;
		private List<WorkloadDto.ListDto> todayDeployedWorkload;
	}
	
	@Getter
	@Setter
	@Builder
	public static class PodSummary {
		private Integer countTotal;
		private Map<String, List<PodList>> podOperatingRate;
		private Map<String, List<PodList>>  podDeployedByNode;
		private List<PodList> podRestartList;
	}
	
	@Getter
	@Setter
	@Builder
	public static class NameCountPaire {
		private String name;
		private Integer count;
	}
	
	@Getter
	@Setter
	@Builder
	public static class PodList {
		private String uid;
		private String name;
		private String namespace;
		private String kind;
		private String status;
		private Integer restart;
		private String startedAt;
		private String createAt;
	}
}
