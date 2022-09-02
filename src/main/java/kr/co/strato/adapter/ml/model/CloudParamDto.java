package kr.co.strato.adapter.ml.model;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CloudParamDto {
	
	@Getter
	@Setter
	public static class ScaleArg {
		
		//클러스터 이름
		private String clusterName;
		
		//Node pool 이름(Optinal)
		private String nodePoolName;
				 
		//NodePool Size
		private Integer nodeCount;
	}
	
	@Getter
	@Setter
	public static class ModifyArg {
		
		//클러스터 이름
		private String clusterName;
		
		//vm 상품 타입
		private String vmType;
		
		//Node pool 이름(Optinal)
		private String nodePoolName;
				
		//NodePool Size
		private Integer nodeCount;
	}
	
}
