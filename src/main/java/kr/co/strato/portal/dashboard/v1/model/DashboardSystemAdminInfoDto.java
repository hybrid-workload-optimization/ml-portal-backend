package kr.co.strato.portal.dashboard.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DashboardSystemAdminInfoDto {
	private int projectCount;
	private int clusterCount;
	
	private int nodeCount;	
	private int masterCount;
	private int workerCount;
	
	private int masterReadyCount;
	private int workerReadyCount;
	
	int totalUtilization;
	int masterUtilization;
	int workerUtilization;
	
	private String nodeUtilizationState;
	
	private int restartWithinTenMinutes;
}
