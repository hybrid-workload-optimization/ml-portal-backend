package kr.co.strato.portal.cluster.model;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class PublicClusterDto {

	@Getter
	@Setter
	@Builder
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Povisioning {
		
		@NotBlank(message="callbackUrl is required")
		private String callbackUrl;
		
		@NotBlank(message="cloudProvider is required")
		private String cloudProvider;
		
		private Map<String, Object> povisioningParam;	
	}
	
	@Getter
	@Setter
	@Builder
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Delete {
		private String callbackUrl;
		private Long clusterIdx;	
	}
}
