package kr.co.strato.adapter.cloud.aks.model;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateArg {
	
	//클러스터 이름
	@NotEmpty(message = "클러스터 이름은 null 또는 공백을 허용하지 않습니다.")
	private String clusterName;
	
	//Kubernetes 버전
	@NotEmpty(message = "쿠버네티스 버전은 null 또는 공백을 허용하지 않습니다.")
	private String kubernetesVersion;
	
	//클러스터 생성 지역
	@NotEmpty(message = "지역은 null 또는 공백을 허용하지 않습니다.")
	private String region;
	
	//vm 상품 타입
	//@NotEmpty(message = "VM 상품유형은 null 또는 공백을 허용하지 않습니다.")
	//private String vmType;
	
	//NodePool Size
	//@Positive(message = "노드 수는 null 또는 0을 허용하지 않으며, 양수만 허용합니다.")
	//private Integer nodeCount;
	
	//Node pool 이름(Optinal)
	//private String nodePoolName;
	
	//기타 클라우드 별 필요 파라메타 정의
	private String apiServerAvailability;
	
	private String nodePoolMode;
	private String nodePoolOsType;
	private String nodePoolType;
	private Integer nodePoolMinCount;
	private Integer nodePoolMaxCount;
	private Integer maxPodsCount;
	private Boolean enableNodePublicIp;
	private String nodePublicIpPrefix;
	private Map<String, String> nodePoolLabels;
	private List<String> nodePoolTaints;
	private String scaleSetPriority;
	private String scaleSetEvictionPolicy;
	private Float spotMaxPrice;
	private Boolean enableVirtualNode;
	private String diskEncryptionSetId;
	
	private Boolean enableRbac;
	private boolean enableAzureRbac;
	private boolean enableAad;
	private List<String> groupObjectIds;
	
	private String networkPlugin;
	private String vnetId;
	private String vnetSubnetId;
	private String serviceCidr;
	private String dnsServiceIp;
	private String dockerBridgeCidr;
	private String dnsPrefix;
	private String outBoundType;
	private String loadBalancerSKU;
	private Boolean enablePrivateCluster;
	private List<String> authorizedIpRanges;
	private String networkPolicy;

	private Boolean enableHttpApplicationRouting;
	private Boolean enableMonitoring;
	private String workspaceResourceId;
	private Boolean azurePolicy;
	
	private Boolean azureKeyvaultSecretsProvider;
	
	private Map<String, String> tags;

	private List<NodePool> nodePools;
	
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	public static class NodePool {
		
		private String nodePoolName;
		
		private String nodePoolMode;
		
		private String nodePoolOsType;
		
		private String vmType;
		
		private String nodePoolType;

		private Integer nodeCount;
		
		private Integer maxPodsCount;
		
		private Integer nodePoolMinCount;
		
		private Integer nodePoolMaxCount;
		
		private String scaleSetPriority;
		
		private String scaleSetEvictionPolicy;
		
		private Float spotMaxPrice;
		
		private Boolean enableNodePublicIp;
		
		private Map<String, String> nodePoolLabels;
		private List<String> nodePoolTaints;
		
	}
}
