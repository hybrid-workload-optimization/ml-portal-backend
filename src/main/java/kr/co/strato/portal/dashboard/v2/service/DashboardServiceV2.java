package kr.co.strato.portal.dashboard.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.config.ApplicationContextProvider;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.ClusterSummary;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto.Overview;
import kr.co.strato.portal.cluster.v2.service.ClusterServiceV2;
import kr.co.strato.portal.dashboard.v2.model.DashboardDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashboardServiceV2 {
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	

	public DashboardDto getDashboardGeneral(UserDto loginUser, Long projectIdx) {
		List<ClusterEntity> clusters = clusterDomainService.getListByProjectIdx(projectIdx);
		
		int clusterCount = clusters.size();
		int nodeCount = 0;
		int workloadCount = 0;
		
		int controlPlaneCount = 0;
		int workerCount = 0;
		
		int controlPlaneReadyCount = 0;
		int workerReadyCount = 0;
		
		int totalUtilization = 0;
		int controlPlaneUtilization = 0;
		int workerUtilization = 0;
		
		String nodeUtilizationState = "Unknown";
		
		List<ClusterSummary> clusterSummaryList = new ArrayList<>();
		
		if(clusters != null && clusters.size() > 0) {
			List<GetClusterOverViewRunnable> runnableList = new ArrayList<>();
			for(ClusterEntity entity : clusters) {			
				ClusterServiceV2 clusterService = ApplicationContextProvider.getBean(ClusterServiceV2.class);
				GetClusterOverViewRunnable runnable = new GetClusterOverViewRunnable(entity, clusterService);
				Executors.newSingleThreadExecutor().submit(runnable);
				runnableList.add(runnable);
			}
			wait(runnableList);
			
			List<Overview> results = runnableList.stream().map(r -> r.getOverview()).collect(Collectors.toList());
			
			
			
			
			for(Overview overview : results) {
				List<NodeDto.ListDto> nodes = overview.getNodes();
				
				nodeCount += nodes.size();
				workloadCount += overview.getClusterSummary().getCountWorkload();
				
				
				clusterSummaryList.add(overview.getClusterSummary());
				
				for(NodeDto.ListDto node : nodes) {
					Boolean isReady = Boolean.parseBoolean(node.getStatus());
					if(node.getRole().contains("control-plane") || node.getRole().contains("master")) {
						controlPlaneCount++;
						if(isReady) {
							controlPlaneReadyCount++;
						}
					} else {
						workerCount++;
						if(isReady) {
							workerReadyCount++;
						}
					}
				}
			}
			int readyCount = controlPlaneReadyCount + workerReadyCount;
			
			totalUtilization = Long.valueOf(Math.round((double) readyCount / (double) nodeCount * 100)).intValue();
			controlPlaneUtilization = Long.valueOf(Math.round((double) controlPlaneReadyCount / (double) controlPlaneCount * 100)).intValue();
			workerUtilization = Long.valueOf(Math.round((double) workerReadyCount / (double) workerCount * 100)).intValue();
			
			//90 이상 Good
			//90 미만 70 이상 Warning
			//70 미만 Bad
			if(totalUtilization >= 90) {
				nodeUtilizationState = "Good";
			} else if(totalUtilization > 70) {
				nodeUtilizationState = "Warning";
			} else {
				nodeUtilizationState = "Bad";
			}
			
		}
		DashboardDto dashboard = DashboardDto.builder()
				.clusterCount(clusterCount)					
				.workloadCount(workloadCount)
				.nodeCount(nodeCount)
				.controlPlaneCount(controlPlaneCount)
				.workerCount(workerCount)
				.controlPlaneReadyCount(controlPlaneReadyCount)
				.workerReadyCount(workerReadyCount)
				.totalUtilization(totalUtilization)
				.controlPlaneUtilization(controlPlaneUtilization)
				.workerUtilization(workerUtilization)
				.clusterSummaryList(clusterSummaryList)
				.nodeUtilizationState(nodeUtilizationState)
				.build();
		
		return dashboard;	
	}
	
	/**
	 * 작업이 완료 될 때 까지 대기.
	 * @param runnables
	 */
	private void wait(List<GetClusterOverViewRunnable> runnables) {
		while(true) {
			int complateCount = 0;			
			for(GetClusterOverViewRunnable r : runnables) {
				if(r.isFinish()) {
					complateCount++;
				}
			}
			
			if(runnables.size() == complateCount) {
				break;
			}
			try {Thread.sleep(100);} catch (InterruptedException e) {}
		}
	}
	
	
	@Data
	class GetClusterOverViewRunnable implements Runnable {
		private boolean isFinish;
		private Overview clusterOverview;
		private ClusterEntity clusterEntity;
		private ClusterServiceV2 clusterService;
		private Overview overview;
		
		public GetClusterOverViewRunnable(ClusterEntity clusterEntity, ClusterServiceV2 clusterService) {
			this.isFinish = false;
			this.clusterEntity = clusterEntity;
			this.clusterService = clusterService;
		}

		@Override
		public void run() {
			try {
				this.overview = clusterService.getOverview(clusterEntity, true);
			} catch (Exception e) {
				log.error("", e);
			} finally {
				this.isFinish = true;
			}
		}
	}
}
