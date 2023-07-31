package kr.co.strato.portal.cluster.v2.model;

import java.util.List;
import java.util.Map;

import kr.co.strato.portal.workload.v1.model.WorkloadDto;
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
		private List<WorkloadDto.List> controlPlaneComponent;
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
		private String provider;
		private String region;
		private String vision;
		private String status;
		private String createAt;
		private String createBy;
		
		private String vpcCidr;
		private String serviceCidr;
		private String podCidr;
		
		private float cpuTotal;
		private float memoryTotal;
		private float cpuUsage;
		private float memoryUsage;
		private float totalStorage;
		
		private Integer countNode;
		private Integer countNamespace;
		private Integer countPV;
		private Integer countWorkload;
		private Integer countPod;
	}
	
	@Getter
	@Setter
	@Builder
	public static class WorkloadSummary {
		private List<WorkloadDto.List> deployments;
		private List<WorkloadDto.List> statefulSets;
		private List<WorkloadDto.List> cronJobs;
		private List<WorkloadDto.List> jobs;
		private List<WorkloadDto.List> replicaSets;
		private List<WorkloadDto.List> daemonSets;
		private List<WorkloadDto.List> todayDeployedWorkload;
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
