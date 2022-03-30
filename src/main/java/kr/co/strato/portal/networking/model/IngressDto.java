package kr.co.strato.portal.networking.model;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class IngressDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto{
	    //TODO validation체크
		private Long kubeConfigId;
		
		 @K8sKind(value = K8sKindType.Ingress)
	    private String yaml;
	}


	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto{
		private Long id;
		private String name;
		private String uid;
		private String ingressClass;
		private LocalDateTime createdAt;
		private Long namespaceIdx;
		private String namespace;
		private String address;
		private String host;
		private Long ingressControllerIdx;
	}

	
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResDetailDto{
    	private Long id;
    	private String name;
    	private String uid;
    	private String ingressClass;
    	private LocalDateTime createdAt;
    	private Long namespaceIdx;
    	private String namespace;
		private String address;
		private String host;
		private Long clusterId;
		private Long clusterIdx;
    	private Long ingressControllerIdx;
    	private List<RuleList> ruleList;
    }
	
	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam{
	    private Long namespaceIdx;
	    private String name;
	}
	
	@Getter
	@Setter
	@ToString
	public static class RuleList {
		private String host;
		private String protocol;
		private String path;
		private String pathType;
		private String service;
		private String port;
		
		
	}
}
