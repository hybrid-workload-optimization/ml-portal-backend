package kr.co.strato.adapter.cloud.cluster.model;

import java.util.List;

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
	
	private List<Node> nodes;
	
	
	@Getter
	@Setter
	public static class Node {
		
		private String name;
		
		private String ip;
		
		private List<String> nodeTypes;
		
	}
}
