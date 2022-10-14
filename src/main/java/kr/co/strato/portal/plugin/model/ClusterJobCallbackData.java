package kr.co.strato.portal.plugin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterJobCallbackData {
	private Long clusterIdx;
	private String clusterName;
	
	//CLUSTER_CREATE, CLUSTER_DELETE, CLUSTER_SCALE, CLUSTER_MODIFY
	private String clusterJobType;
	
	//start, finish
	private String status;
	
	//success, fail
	private String result;
	
	//작업 실패 시 메세지
	private String message;
}
