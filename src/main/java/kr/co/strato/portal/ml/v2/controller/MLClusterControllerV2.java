package kr.co.strato.portal.ml.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.ml.v2.model.MLClusterDto.ClusterDetail;
import kr.co.strato.portal.ml.v2.model.MLClusterDto.ClusterList;
import kr.co.strato.portal.ml.v2.service.MLClusterService;

@RequestMapping("/api/v2/ml/cluster")
@Api(tags = {"Cluster 관리 v2"})
@RestController
public class MLClusterControllerV2 {
	
	@Autowired
	private MLClusterService mlClusterService;

	/**
	 * 머신러닝을 위한 클러스터 리스트 조회.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Cluster 리스트 정보 조회", description = "클러스터 정보를 조회한다")
	@GetMapping("/list")
	public ResponseWrapper<List<ClusterList>> getClusters() {
		List<ClusterList> url = mlClusterService.getClusterList();
		return new ResponseWrapper<>(url);
	}
	
	/**
	 * 머신러능을 위한 클러스터 상세 정보 조회.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Cluster 상세 정보 조회", description = "클러스터 상세 정보를 조회한다(Prometheus URL, 노드 및 사용량 정보 포함)")
	@GetMapping("/{clusterIdx}")
	public ResponseWrapper<ClusterDetail> getCluster(@PathVariable Long clusterIdx) {
		ClusterDetail url = mlClusterService.getClusterDetail(clusterIdx);
		return new ResponseWrapper<>(url);
	}
	
}
