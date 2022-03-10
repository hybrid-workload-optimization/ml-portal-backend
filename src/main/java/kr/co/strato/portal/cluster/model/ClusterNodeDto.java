package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ClusterNodeDto {
	
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
		private String ip;
		private String status;
		private String k8sVersion;
		private float allocatedCpu;
		private float allocatedMemory;
		private LocalDateTime createdAt;
		private Long clusterIdx;
		private List<String> role;
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
		private String k8sVersion;
		private float allocatedCpu;
		private float allocatedMemory;
		private LocalDateTime createdAt;
		private String podCidr;
		private String osImage;
		private String kernelVersion;
		private String architecture;
		private String kubeletVersion;
		private String kubeproxyVersion;
		private Long clusterIdx;
		
		private HashMap<String, Object> annotation;
		private HashMap<String, Object> label;
		private HashMap<String, Object> condition;
		private List<String>  role;
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

