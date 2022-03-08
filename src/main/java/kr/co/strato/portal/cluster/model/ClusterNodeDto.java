package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;
import java.util.HashMap;

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
		private String role;
		
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
		private String annotation;
		private String label;
		private String condition;
		private String role;
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

