package kr.co.strato.portal.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectClusterDto {

	private Long clusterIdx;
	private Long projectIdx;
	
	private String provider;
	private Long nodeCount;
	private String providerVersion;
	private String clusterName;
	private String description;
	private String createdAt;
	private String addedAt;
}