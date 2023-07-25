package kr.co.strato.portal.workload.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class WorkloadDto {
	
	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class List {
		private String uid;
		private String name;
		private String namespace;
		private String kind;
		private Integer podCountTotal;
		private Integer podCountReady;
		private String health;
		private Map<String, String> labels;
		private String createAt;		
		
	}

	@Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class SearchParam {
        private Long clusterIdx;
        private String namespace;
        private String[] kinds;
    }
}
