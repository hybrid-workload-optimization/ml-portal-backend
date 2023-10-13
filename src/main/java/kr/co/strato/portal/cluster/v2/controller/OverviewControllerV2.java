package kr.co.strato.portal.cluster.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v2.model.ClusterOverviewDto;
import kr.co.strato.portal.cluster.v2.service.ClusterServiceV2;
import lombok.extern.slf4j.Slf4j;

@Api(tags = {"Cluster > Overview V2"})
@Slf4j
@RequestMapping("/api/v1/cluster")
@RestController
public class OverviewControllerV2 {

	@Autowired
	private ClusterServiceV2 clusterService;
	
	
	@Operation(summary = "Overview 정보 조회", description = "Overview 정보를 조회한다.")
	@GetMapping("/{clusterIdx}/overview")
    public ResponseWrapper<ClusterOverviewDto.Overview> getOverview(@PathVariable(required = true) Long clusterIdx) {
		ClusterOverviewDto.Overview result = clusterService.getOverview(clusterIdx);
		return new ResponseWrapper<>(result);
	}
	
}
