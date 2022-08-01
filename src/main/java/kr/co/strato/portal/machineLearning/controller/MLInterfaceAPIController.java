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
	@PostMapping("/api/v1/ml/apply")
	public ResponseWrapper<String> apply(@RequestBody MLDto.ApplyArg applyDto) {
		String mlId = apiService.apply(applyDto);
		return new ResponseWrapper<>(mlId);
	}
	
	/**
	 * ML Step 완료 시 호출(전처리, 검증, 학습, 추론)
	 * @param mlId
	 */
	@PutMapping("/api/v1/ml/finish/{mlId}")
	public ResponseWrapper<String> finish(@PathVariable("mlId") String mlId) {
		apiService.finish(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML Step 중지 및 삭제
	 */
	@DeleteMapping("/api/v1/ml/delete/{mlId}")
	public ResponseWrapper<String> delete(@PathVariable("mlId") String mlId) {
		boolean isDelete = apiService.delete(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML 리스트
	 * @param pageRequest
	 */
	@PostMapping("/api/v1/ml/list")
	public ResponseWrapper<Object> mlList(@RequestBody ListArg param) {
		return new ResponseWrapper<>(apiService.getMlList(param));
	}
	
	/**
	 * ML 상세 정보
	 * @param mlId
	 */
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
	@GetMapping("/api/v1/ml/cluster/{clusterId}/prometheusUrl")
	public ResponseWrapper<String> getPrometheusUrl(@PathVariable("clusterId") Long clusterId) {
		return null;
	}
	
	
	/**
	 * 클러스터 Scale 조정
	 */
	@PostMapping("/api/v1/ml/cluster/scale")
	public ResponseWrapper<String> scale(@RequestBody ScaleArgDto scaleDto) {
		return null;
	}
	
	
	/**
	 * 지속형 클러스터 리스트 요청
	 */
	@GetMapping("/api/v1/ml/cluster/list")
	public ResponseWrapper<List<MLClusterDto.List>> clusterList(PageRequest pageRequest) {
		return null;
	}
	
	/**
	 * 지속형 클러스터 상세 정보 요청
	 */
	@GetMapping("/api/v1/ml/cluster/{clusterId}")
	public ResponseWrapper<MLClusterDto.Detail> clusterDetail(@PathVariable("clusterId") Long clusterId) {
		return null;
	}
}
