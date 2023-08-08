package kr.co.strato.portal.ml.v1.model;

import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MLClusterDto {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class List {
		private Long clusterId;
		private String clusterName;
		private String description;
		private String status;
		private int nodeCount;
		private String provider;
		private String kubeVersion;
		private String createdAt;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Detail {
		private Long clusterId;
		private String clusterName;
		private String description;
		private String status;
		private int nodeCount;
		private String provider;
		private String kubeVersion;
		private String createdAt;
		private java.util.List<ClusterNodeDto.ResDetailDto> nodes;
	}
	
}
