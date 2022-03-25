package kr.co.strato.portal.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.portal.cluster.model.ClusterNodeDto.ResListDto;
import kr.co.strato.portal.dashboard.model.SystemAdminNodeStateDto;
import kr.co.strato.portal.dashboard.service.DashboardService;

@RestController
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping("/api/v1/dashboard/systemAdmin/nodeState")
    public SystemAdminNodeStateDto getNodeState(
    		@RequestParam(required = false) Long projectIdx,
    		@RequestParam(required = false) Long clusterIdx) throws Exception {
		return dashboardService.getNodeState(projectIdx, clusterIdx);
    }
	
	@GetMapping("/api/v1/dashboard/systemAdmin/nodeList")
    public List<ResListDto> getNodeList(
    		@RequestParam(required = false) Long projectIdx,
    		@RequestParam(required = false) Long clusterIdx) throws Exception {
		return dashboardService.getNodeList(projectIdx, clusterIdx);
    }
}
