package kr.co.strato.adapter.k8s.ingressController.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIngressControllerParam {
	private Long kubeConfigId;
	private String ingressControllerType;	
	private Integer replicas;
	private Integer httpPort;
	private Integer httpsPort;
	private Boolean isDefault;
	private String serviceType;
	private List<String> externalIPs;
}
