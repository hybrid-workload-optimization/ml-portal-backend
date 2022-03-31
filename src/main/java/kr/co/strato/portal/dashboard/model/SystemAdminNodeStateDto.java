package kr.co.strato.portal.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SystemAdminNodeStateDto {
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
