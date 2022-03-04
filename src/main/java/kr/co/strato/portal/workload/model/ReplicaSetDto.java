package kr.co.strato.portal.workload.model;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplicaSetDto {
	
	private Long projectIdx;
	
	private Long clusterIdx;
	
	private Long namespaceIdx;
	
	private Long replicaSetIdx;
	
	private String yaml;
	
	@Getter
	@Setter
	public static class List {
		private Long replicaSetIdx;
		private String name;
		private String namespace;
		private HashMap<String, Object> label;
		private int podCount;
		private int podTotalCount;
		private String image;
		private String age;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
	}
}
