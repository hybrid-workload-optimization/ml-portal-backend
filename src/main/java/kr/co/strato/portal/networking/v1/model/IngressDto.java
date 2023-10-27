package kr.co.strato.portal.networking.v1.model;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.strato.global.validation.annotation.K8sKind;
import kr.co.strato.global.validation.model.K8sKindType;
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
	public static class ReqCreateDto {
		// TODO validation체크
		private Long kubeConfigId;

		@K8sKind(value = K8sKindType.Ingress)
		private String yaml;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto {
		private Long id;
		private String name;
		private String uid;
		private String ingressClass;
		private LocalDateTime createdAt;
		private Long namespaceIdx;
		private String namespace;
		private String clusterName;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResDetailDto {
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
		private List<RuleList> ruleList;
		private String clusterName;
		private Long projectIdx;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
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
		private List<String> endpoints;

	}
}
