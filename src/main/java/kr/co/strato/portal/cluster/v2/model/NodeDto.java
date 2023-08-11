package kr.co.strato.portal.cluster.v2.model;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.NodeCondition;
import kr.co.strato.portal.workload.v2.model.PodDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NodeDto {

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
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
	public static class DetailDto extends ListDto {
		private String k8sVersion;
		private float allocatedCpu;
		private float allocatedMemory;
		private String podCidr;
		private String osImage;
		private String kernelVersion;
		private String architecture;
		private String kubeletVersion;
		//private String kubeproxyVersion;
		private Map<String, String> annotation;
		private Map<String, String> label;
		private List<NodeCondition> conditions;
		private List<PodDto> pods;
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
