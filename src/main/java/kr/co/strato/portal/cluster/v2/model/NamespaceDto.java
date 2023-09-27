package kr.co.strato.portal.cluster.v2.model;

import java.util.Map;

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
public class NamespaceDto {
	
	private String uid;
	private String name;
	private String status;
	private String createdAt;
	private Map<String, String> annotation;
	private Map<String, String> label;
	private String podStatus;
	private ResourceQuotaDto resourceQuota;
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class CreateDto {
	    private Long clusterIdx;
	    
	    @K8sKind(value = K8sKindType.Namespace)
	    private String yaml;
	}


	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ListDto {
		private String uid;
    	private String name;
    	private String status;
    	private String createdAt;
    	private Map<String, String> annotation;
    	private Map<String, String> label;
    	private String podStatus;
    	private ResourceQuotaDto resourceQuota;
	}

	
    @Getter
    @Setter
    public static class DetailDto extends ListDto {
    	
    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchList {
	    private Long clusterIdx;
	    private String name;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class Delete {
	    private Long clusterIdx;
	    private String name;
	}
	
	@Getter
    @Setter
    @Builder
    public static class ResourceQuotaDto {
		private double hardRequestsCpu;
		private double hardRequestsMemory;		
		private double hardLimitsCpu;
		private double hardLimitsMemory;	
		
		private double usedRequestsCpu;
		private double usedRequestsMemory;
		private double usedLimitsCpu;
		private double usedLimitsMemory;
		
		private double cpuRequestsFraction;
		private double memoryRequestsFraction;		
		private double cpuLimitsFraction;
		private double memoryLimitsFraction;
    }
}
