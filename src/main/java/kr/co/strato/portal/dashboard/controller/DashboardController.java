package kr.co.strato.portal.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.portal.dashboard.model.DashboardSystemAdminDto;
import kr.co.strato.portal.dashboard.service.DashboardService;

@RestController
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;
	
	@GetMapping("/api/v1/dashboard/system-admin")
    public DashboardSystemAdminDto getDashboardSystemAdminInfo(
    		@RequestParam(required = false) Long projectIdx,
    		@RequestParam(required = false) Long clusterIdx) throws Exception {
		return dashboardService.getDashboardInfoForSystemAdmin(projectIdx, clusterIdx);
    }
}
