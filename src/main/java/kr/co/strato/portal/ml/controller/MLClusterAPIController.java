package kr.co.strato.portal.ml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ModifyArgDto;
import kr.co.strato.portal.cluster.model.ScaleArgDto;
import kr.co.strato.portal.ml.service.MLClusterAPIAsyncService;

@RequestMapping("/api/v1/ml/cluster")
@Api(tags = {"ML Cluster 관련 API(생성/Scale 조정/삭제/상세 정보)"})
@RestController
public class MLClusterAPIController {
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterService;
	
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
		mlClusterService.scaleJobCluster(scaleDto);
		return new ResponseWrapper<>();
	}
	
	/**
	 * 클러스터 Scale 조정
	 */
	@Operation(summary = "클러스터 노드풀 변경", description = "클러스터 노드풀 변경")
	@PostMapping("/modify")
	public ResponseWrapper<String> modify(@RequestBody ModifyArgDto modifyDto) {
		mlClusterService.modifyJobCluster(modifyDto);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML Step 중지 및 삭제
	 */
	@Operation(summary = "ML Cluster 삭제", description = "Machine learning 클러스터 삭제")
	@DeleteMapping("/delete/{clusterIdx}")
	public ResponseWrapper<String> delete(@PathVariable("clusterIdx") Long clusterIdx) {
		mlClusterService.deleteMlCluster(clusterIdx);
		return new ResponseWrapper<>();
	}
}
