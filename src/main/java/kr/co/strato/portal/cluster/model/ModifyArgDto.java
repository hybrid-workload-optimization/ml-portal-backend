package kr.co.strato.portal.cluster.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModifyArgDto {
	
	public static final Integer WorkJobDto = null;

	//스케일 조정하려는 클러스터 ID
	private Long clusterIdx;
	
	//vm 상품 타입
	private String vmType;
			
	//NodePool Size
	private Integer nodeCount;
	
	//스케일 조정 후 결과 callback 받을 url
	private String callbackUrl;
	
}
