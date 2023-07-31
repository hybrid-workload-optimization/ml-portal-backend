package kr.co.strato.portal.workload.v1.model;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaemonSetDto {

private Long projectIdx;
	
	private Long clusterIdx;
	
	private Long namespaceIdx;
	
	private Long daemonSetIdx;
	
	private String yaml;
	
	@Getter
	@Setter
	public static class List {
		private Long daemonSetIdx;
		private String name;
		private String namespace;
		private HashMap<String, Object> label;
		private int runningPod;
		private int desiredPod;
		private String image;
		private String age;
		private String clusterName;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long daemonSetIdx;
		private String name;
		private String namespace;
		private String uid;
		private HashMap<String, Object> label;
		private HashMap<String, Object> annotation;
		private String createdAt;
		private HashMap<String, Object>  selector;
		private String image;
		private int runningPod;
		private int desiredPod;
		private Long clusterId;
		private Long projectIdx;
		private Long clusterIdx;
		private String clusterName;
		
		private ArrayList<PodDto.ResListDto> pods;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
	}
}
