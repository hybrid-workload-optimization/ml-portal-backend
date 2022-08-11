package kr.co.strato.portal.machineLearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.machineLearning.model.MLClusterDto;
import kr.co.strato.portal.machineLearning.model.ScaleArgDto;
import kr.co.strato.portal.machineLearning.service.MLClusterAPIService;

@RequestMapping("/api/v1/ml/cluster")
@RestController
public class MLClusterAPIController {
	
	@Autowired
	private MLClusterAPIService mlClusterService;
	
	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Prometheus URL", description = "클러스터 별 Prometheus URL 요청")
	@GetMapping("/api/v1/ml/cluster/{clusterId}/prometheusUrl")
	public ResponseWrapper<String> getPrometheusUrl(@PathVariable("clusterId") Long clusterId) {
		String url = mlClusterService.getPrometheusUrl(clusterId);
		return new ResponseWrapper<>(url);
	}
	
	
	/**
	 * 클러스터 Scale 조정
	 */
	@Operation(summary = "Scale 조정", description = "Cluster Scale 조정(Scale-In, Scale-Out)")
	@PostMapping("/scale")
	public ResponseWrapper<String> scale(@RequestBody ScaleArgDto scaleDto) {
		return null;
	}
	
	
	/**
	 * 지속형 클러스터 리스트 요청
	 */
	@Operation(summary = "Cluster 리스트", description = "지속형 클러스터(ML 서비스를 위한) 리스트 요청")
	@GetMapping("/list")
	public ResponseWrapper<List<MLClusterDto.List>> clusterList() {
		return new ResponseWrapper<>(mlClusterService.getServiceClusterList());
	}
	
	/**
	 * 지속형 클러스터 상세 정보 요청
	 */
	@Operation(summary = "Cluster 상세", description = "지속형 클러스터(ML 서비스를 위한) 상세 정보 요청")
	@GetMapping("/{clusterId}")
	public ResponseWrapper<MLClusterDto.Detail> clusterDetail(@PathVariable("clusterId") Long clusterId) {
		return new ResponseWrapper<>(mlClusterService.getServiceClusterDetail(clusterId));
	}
}
