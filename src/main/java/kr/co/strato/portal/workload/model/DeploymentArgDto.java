package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class DeploymentArgDto {
	private Long deploymentIdx;
	private Long projectIdx;
	private Long namespaceIdx;

	private Long clusterId;
	private String yaml;
	private Long clusterIdx;
	
	@Getter
    @Setter
    @NoArgsConstructor
    public static class ListParam{
		private Long clusterIdx;
		private String namespace;
    }
	
	@Getter
    @Setter
    @NoArgsConstructor
    public static class DeleteParam{
		private Long clusterIdx;
		private String namespace;
		private String name;
    }
	
	@Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateParam {
		private Long clusterIdx;
		private String namespace;
		private String name;
		private String yaml;
    }
}
