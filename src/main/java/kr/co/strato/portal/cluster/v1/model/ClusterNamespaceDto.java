package kr.co.strato.portal.cluster.v1.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ClusterNamespaceDto {
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto {
	    //TODO validation체크
	    private Long clusterIdx;
	    
	    @K8sKind(value = K8sKindType.Namespace)
	    private String yaml;
	}


	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResListDto{
		private Long id;
    	private String name;
    	private String uid;
    	private String ip;
    	private String status;
    	private LocalDateTime createdAt;
    	private Long clusterIdx;
    	private HashMap<String, Object> annotation;
    	private HashMap<String, Object> label;
	}

	
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResDetailDto{
    	private Long id;
    	private String name;
    	private String uid;
    	private String ip;
    	private String status;
    	private LocalDateTime createdAt;
    	private Long clusterIdx;
    	private Long clusterId;
    	private HashMap<String, Object> annotation;
    	private HashMap<String, Object> label;
    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam{
	    private Long clusterIdx;
	    private String name;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class DeleteParam{
	    private Long clusterIdx;
	    private String name;
	}
}
