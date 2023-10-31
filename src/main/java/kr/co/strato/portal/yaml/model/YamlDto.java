package kr.co.strato.portal.yaml.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YamlDto {
	
	private Long yamlIdx;
	private String kind;	
	private String name;
	private String namespace;
	private String yaml;
	private String createAt;
	private String createBy;
	
	@Getter
    @Setter
    @NoArgsConstructor
    public static class ApplyDto {
        private Long clusterIdx;
        private String yaml;
    }
	
	@Getter
    @Setter
    @Builder
	@NoArgsConstructor
	@AllArgsConstructor
    public static class ApplyResultDto {
		private boolean success;	
		private String errorMessage;
		private List<ApplyResourceDto> resources;
    }

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ApplyResourceDto {
		private String uid;
		private String name;
		private String namespace;
		private String kind;
		private Map<String, String> labels;
		private String createAt;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long clusterIdx;
		private String kind;
        private String name;
        private String namespace;
	}
}
