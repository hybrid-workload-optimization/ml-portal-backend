package kr.co.strato.adapter.k8s.common.model;

import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ApplyResult {
	
	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {
		private boolean success;	
		private String errorMessage;
		private String resources;
	}
	
	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Result {
		private boolean success;	
		private String errorMessage;
		private List<HasMetadata> resources;
	}
}
