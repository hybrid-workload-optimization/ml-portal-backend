package kr.co.strato.portal.networking.model;

import java.util.List;

import kr.co.strato.adapter.k8s.ingressController.model.ServicePort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class IngressControllerDto {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto {
		private Long id;
		private Long clusterIdx;
		private String name;	
		private Integer replicas;
		private String serviceType;
		private Integer httpPort;
		private Integer httpsPort;
		private boolean isDefault;
		private List<String> externalIp;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResListDto {
		private Long id;
		private String name;	
		private Integer replicas;
		private List<String> externalIp;	
		private List<ServicePort> port;
		private String serviceType;
		private String ingressClass;
		private String createdAt;
		private boolean isDefault;
		private Long clusterIdx;
		private String clusterName;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResDetailDto {
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class SearchParam {
		private Long ingressControllerIdx;
	}
}
