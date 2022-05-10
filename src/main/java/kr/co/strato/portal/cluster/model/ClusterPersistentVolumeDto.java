package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ClusterPersistentVolumeDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto{
	    //TODO validation체크
	    private Long clusterIdx;
	    @K8sKind(value = K8sKindType.PersistentVolume)
	    private String yaml;
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto{
		private Long id;
		private String name;
		private String uid;
		private String ip;
		private String status;
		private LocalDateTime createdAt;
		private String accessMode;
		private String claim;
		private String reclaim;
		private String reclaimPolicy;
		private Long storageClassIdx;
		private String storageClassName;
		private String type;
		private String path;
		private String resourceName;
		private int size;
    	private Long clusterIdx;
    	private HashMap<String, Object> annotation;
    	private HashMap<String, Object> label;
	}

	
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
		private Long id;
		private String name;
		private String uid;
		private String ip;
		private String status;
		private LocalDateTime createdAt;
		private String accessMode;
		private String claim;
		private String reclaim;
		private String reclaimPolicy;
		private Long storageClassIdx;
		private String storageClassName;
		private String type;
		private String path;
		private String resourceName;
		private int size;
    	private Long clusterIdx;
    	private Long clusterId;
    	private HashMap<String, Object> annotation;
    	private HashMap<String, Object> label;
    	private String yaml;
    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam{
	    private Long clusterIdx;
	    private String name;
	}
	
	

}
