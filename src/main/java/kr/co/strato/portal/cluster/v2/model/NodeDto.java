package kr.co.strato.portal.cluster.v2.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class NodeDto {

	@Getter
	@Setter
	@Builder
	public static class ListDto {
		private String uid;
		private String name;
		private String ip;
		private String status;
		private String podStatus;
		private List<String> role;
		private NodeUsageDto usageDto;
		private Map<String, String> labels;
		private String createdAt;
	}
	
	@Getter
    @Setter
    @Builder
    public static class NodeUsageDto {
    	private double allocatedPods;
    	private double podCapacity;
    	private double podFraction;
    	private double cpuCapacity;
    	private double cpuLimits;
    	private double cpuLimitsFraction;
    	private double cpuRequests;
    	private double cpuRequestsFraction;
    	private double memoryCapacity;
    	private double memoryLimits;
    	private double memoryLimitsFraction;
    	private double memoryRequests;
    	private double memoryRequestsFraction;
    }
}
