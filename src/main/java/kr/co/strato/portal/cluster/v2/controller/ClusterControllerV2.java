package kr.co.strato.portal.cluster.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v2.service.ClusterServiceV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/v1/cluster")
@RestController
public class ClusterControllerV2 {

	@Autowired
	private ClusterServiceV2 clusterService;
	
	@GetMapping("/{clusterIdx}/overview")
    public ResponseWrapper<Object> getOverview(@PathVariable(required = true) Long clusterIdx) {
		Object result = clusterService.getOverview(clusterIdx);
		return new ResponseWrapper<>(result);
	}
	
}
