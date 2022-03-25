package kr.co.strato.adapter.cloud.cluster.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class ClusterCloudDto {

	private String clusterName;
	
	private String provider;
	
	private String userName;
	
	private String kubesprayVersion;
	
	private String callbackUrl;
	
	private Long workJobIdx;
	
	private ArrayList<Node> nodes;
	
	
	@Getter
	@Setter
	@ToString
	public static class Node {
		
		private String name;
		
		private String ip;
		
		private ArrayList<String> nodeTypes;
		
	}
}
