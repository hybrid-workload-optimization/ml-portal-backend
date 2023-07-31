package kr.co.strato.portal.cluster.v1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScaleArgDto {
	
	//스케일 조정하려는 클러스터 ID
	private Long clusterIdx;
	
	//gpu, normal(default)
	private String nodeType;
	
	//노드 수
	private Integer nodeCount;
	
	//스케일 조정 이유
	private String reason;
	
	//스케일 조정 후 결과 callback 받을 url
	private String callbackUrl;
}
