package kr.co.strato.portal.dashboard.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.dashboard.v2.model.DashboardDto;
import kr.co.strato.portal.dashboard.v2.service.DashboardServiceV2;

@Api(tags = {"Dashbard V2 API"})
@RequestMapping("/api/v2/dashboard")
@RestController
public class DashboardControllerV2 extends CommonController {
	
	@Autowired
	private DashboardServiceV2 dashboardService;

	@ApiOperation(value="Dashbard 데이터 조회")
	@GetMapping("/{projectIdx}")
    public ResponseWrapper<DashboardDto> getDashboardGeneral(
    		@PathVariable(required = true) Long projectIdx) throws Exception {
		DashboardDto result = dashboardService.getDashboardGeneral(getLoginUser(), projectIdx);
		return new ResponseWrapper<>(result);
    }
	
}
