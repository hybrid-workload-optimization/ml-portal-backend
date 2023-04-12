package kr.co.strato.portal.project.model;

import java.util.ArrayList;

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
	private Long clusterId;
	private String clusterName;
	private String description;
	private String provisioningType;
	private String provisioningStatus;
	private String status;
	private String createdAt;
	private String addedAt;
	private ArrayList<String> problem;
	
}