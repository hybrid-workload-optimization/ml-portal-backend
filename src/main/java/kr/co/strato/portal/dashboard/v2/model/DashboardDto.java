package kr.co.strato.portal.dashboard.v2.model;

import java.util.List;

import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.ClusterSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
	private int clusterCount;
	private int workloadCount;
	
	private int nodeCount;	
	private int controlPlaneCount;
	private int workerCount;
	
	private int controlPlaneReadyCount;
	private int workerReadyCount;
	
	int totalUtilization;
	int controlPlaneUtilization;
	int workerUtilization;
	
	private String nodeUtilizationState;
	private int restartWithinTenMinutes;
	
	private List<ClusterSummary> clusterSummaryList;
}
