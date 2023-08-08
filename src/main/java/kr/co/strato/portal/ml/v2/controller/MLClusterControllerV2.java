package kr.co.strato.portal.ml.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.ml.v2.service.MLClusterService;

@RequestMapping("/api/v2/ml/cluster")
@Api(tags = {"ML Cluster 정보 조회 API"})
@RestController
public class MLClusterControllerV2 {
	
	@Autowired
	private MLClusterService mlClusterService;

	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Cluster 리스트 정보 조회", description = "클러스터 정보를 조회한다(노드 및 사용량 정보 포함)")
	@GetMapping("/list")
	public ResponseWrapper<Object> getClusters() {
		Object url = mlClusterService.getClusterList();
		return new ResponseWrapper<>(url);
	}
	
}
