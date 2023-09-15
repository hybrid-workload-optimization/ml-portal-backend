package kr.co.strato.portal.ml.v2.model;

import java.util.List;

import kr.co.strato.portal.cluster.v2.model.NodeDto;
import lombok.Getter;
import lombok.Setter;

public class MLClusterDto {

	@Getter
	@Setter
	public static class ClusterList {
		private Long clusterIdx;
		private String name;
		private String description;
		private String provider;
		private String region;
		private String vision;
		private String status;
		private String createAt;
	}
	
	@Getter
	@Setter
	public static class ClusterDetail extends ClusterList {
		private String prometheusUrl;
		private List<NodeDto.ListDto> nodes;
	}
}
