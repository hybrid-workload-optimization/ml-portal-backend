package kr.co.strato.portal.networking.v2.model;

import java.util.List;

import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngressDto extends WorkloadCommonDto {
	
	private String ingressClass;
	private String address;
	private String host;
	private List<IngressRuleDto> ruleList;
	

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class IngressRuleDto {
		private String host;
		private String protocol;
		private String path;
		private String pathType;
		private String service;
		private Integer port;
		private List<String> endpoints;

	}
}
