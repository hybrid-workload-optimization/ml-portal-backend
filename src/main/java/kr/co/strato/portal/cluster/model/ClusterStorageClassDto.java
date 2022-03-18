package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import kr.co.strato.portal.networking.model.IngressDto.RuleList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ClusterStorageClassDto {
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto{
	    //TODO validation체크
	    private Long clusterIdx;
	    private String yaml;
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto{
		private Long id;
		private String name;
		private String uid;
		private String status;
		private LocalDateTime createdAt;
		private String provider;
		private String type;
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
    	private String status;
    	private LocalDateTime createdAt;
    	private String provider;
    	private String type;
    	private Long clusterIdx;
    	private HashMap<String, Object> annotation;
    	private HashMap<String, Object> label;
    	private List<PvList> pvList;
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
    public static class PvList{
    	private Long id;
    	private String name;
    	private String status;
		private String accessMode;
		private int size;
		private String reclaimPolicy;
		private String claim;
		private LocalDateTime createdAt;
    }
	
}
