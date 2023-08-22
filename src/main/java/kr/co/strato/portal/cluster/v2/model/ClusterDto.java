package kr.co.strato.portal.cluster.v2.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ClusterDto {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Status {
		private Long clusterIdx;
		private String status;
		private int nodeCount;
		private ArrayList<String> problem;
	}
	
}
