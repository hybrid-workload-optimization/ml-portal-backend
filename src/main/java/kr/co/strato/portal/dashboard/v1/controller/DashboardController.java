package kr.co.strato.portal.dashboard.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto.ResListDetailDto;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.dashboard.v1.model.SystemAdminNodeStateDto;
import kr.co.strato.portal.dashboard.v1.service.DashboardService;

@RestController
public class DashboardController extends CommonController {
	
	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping("/api/v1/dashboard/systemAdmin/nodeState")
    public ResponseWrapper<SystemAdminNodeStateDto> getNodeState(
    		@RequestParam(required = false) Long projectIdx,
    		@RequestParam(required = false) Long clusterIdx) throws Exception {
		SystemAdminNodeStateDto result = dashboardService.getNodeState(getLoginUser(), projectIdx, clusterIdx);
		return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/dashboard/systemAdmin/nodeList")
    public ResponseWrapper<List<ResListDetailDto>> getNodeList(
    		@RequestParam(required = false) Long projectIdx,
    		@RequestParam(required = false) Long clusterIdx) throws Exception {
		List<ResListDetailDto> result = dashboardService.getNodeList(getLoginUser(), projectIdx, clusterIdx);
		return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/dashboard/common/nodeId")
    public ResponseWrapper<Long> getNodeIdByNodeName(
    		@RequestParam(required = true) Long clusterIdx,
    		@RequestParam(required = true) String nodeName) throws Exception {
		Long nodeId = dashboardService.getNodeId(clusterIdx, nodeName);
		return new ResponseWrapper<>(nodeId);
    }
}
