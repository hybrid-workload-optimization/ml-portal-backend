package kr.co.strato.portal.machineLearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.machineLearning.model.MLClusterDto;
import kr.co.strato.portal.machineLearning.model.MLDto;
import kr.co.strato.portal.machineLearning.model.ScaleArgDto;
import kr.co.strato.portal.machineLearning.model.MLDto.ListArg;
import kr.co.strato.portal.machineLearning.service.MLInterfaceAPIService;

@RestController
public class MLInterfaceAPIController {
	
	@Autowired
	private MLInterfaceAPIService apiService;

	/**
	 * ML Step 시작
	 * @param applyDto
	 */
	@Operation(summary = "ML Step 시작", description = "Machine learning 시작 API")
	@PostMapping("/api/v1/ml/apply")
	public ResponseWrapper<String> apply(@RequestBody MLDto.ApplyArg applyDto) {
		String mlId = apiService.apply(applyDto);
		return new ResponseWrapper<>(mlId);
	}
	
	/**
	 * ML Step 완료 시 호출(전처리, 검증, 학습, 추론
	 * @param mlId
	 */
	@Operation(summary = "ML Step 완료", description = "서비스 단계를 제외한 전처리, 검증, 학습, 추론 완료 시 호출 (KETI에서 호출)")
	@PutMapping("/api/v1/ml/finish/{mlId}")
	public ResponseWrapper<String> finish(@PathVariable("mlId") String mlId) {
		apiService.finish(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML Step 중지 및 삭제
	 */
	@Operation(summary = "ML Step 삭제", description = "Machine learning 중지 및 삭제")
	@DeleteMapping("/api/v1/ml/delete/{mlId}")
	public ResponseWrapper<String> delete(@PathVariable("mlId") String mlId) {
		boolean isDelete = apiService.delete(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML 리스트
	 * @param pageRequest
	 */
	@Operation(summary = "ML 리스트", description = "ML 리스트 요청")
	@PostMapping("/api/v1/ml/list")
	public ResponseWrapper<Object> mlList(@RequestBody ListArg param) {
		return new ResponseWrapper<>(apiService.getMlList(param));
	}
	
	/**
	 * ML 상세 정보
	 * @param mlId
	 */
	@Operation(summary = "ML 상세", description = "ML 상세 정보")
	@GetMapping("/api/v1/ml/{mlId}")
	public ResponseWrapper<MLDto.Detail> mlDetail(@PathVariable("mlId") String mlId) {
		MLDto.Detail detail = apiService.getMl(mlId);
		return new ResponseWrapper<>(detail);
	}
	
	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Prometheus URL", description = "클러스터 별 Prometheus URL 요청")
	@GetMapping("/api/v1/ml/cluster/{clusterId}/prometheusUrl")
	public ResponseWrapper<String> getPrometheusUrl(@PathVariable("clusterId") Long clusterId) {
		String url = apiService.getPrometheusUrl(clusterId);
		return new ResponseWrapper<>(url);
	}
	
	
	/**
	 * 클러스터 Scale 조정
	 */
	@Operation(summary = "Scale 조정", description = "Cluster Scale 조정(Scale-In, Scale-Out)")
	@PostMapping("/api/v1/ml/cluster/scale")
	public ResponseWrapper<String> scale(@RequestBody ScaleArgDto scaleDto) {
		return null;
	}
	
	
	/**
	 * 지속형 클러스터 리스트 요청
	 */
	@Operation(summary = "Cluster 리스트", description = "지속형 클러스터(ML 서비스를 위한) 리스트 요청")
	@GetMapping("/api/v1/ml/cluster/list")
	public ResponseWrapper<List<MLClusterDto.List>> clusterList(PageRequest pageRequest) {
		return null;
	}
	
	/**
	 * 지속형 클러스터 상세 정보 요청
	 */
	@Operation(summary = "Cluster 상세", description = "지속형 클러스터(ML 서비스를 위한) 상세 정보 요청")
	@GetMapping("/api/v1/ml/cluster/{clusterId}")
	public ResponseWrapper<MLClusterDto.Detail> clusterDetail(@PathVariable("clusterId") Long clusterId) {
		return null;
	}
}
