
package kr.co.strato.portal.cluster.v2.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PersistentVolumeDto {
	
	@Getter
	@Setter
	@Builder
	public static class ListDto {
		private String uid;
		private String name;
		private String status;		
		private String accessMode;
		private String claim;
		private String reclaim;
		private String reclaimPolicy;
		private String storageClass;
		private String type;
		private double size;
    	private Map<String, String> annotations;
    	private Map<String, String> labels;
    	private String createdAt;
	}
}
