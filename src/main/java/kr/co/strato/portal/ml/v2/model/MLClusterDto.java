package kr.co.strato.portal.ml.v2.model;

import java.util.List;

import kr.co.strato.portal.cluster.v2.model.NodeDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MLClusterDto {

	@Getter
	@Setter
	@Builder
	public static class Cluster {
		Long clusterIdx;
		private String name;
		private String description;
		private String provider;
		private String region;
		private String vision;
		private String status;
		private String createAt;
		private String prometheusUrl;
		List<NodeDto.ListDto> nodes;
	}
}
