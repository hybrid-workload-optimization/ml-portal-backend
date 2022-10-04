package kr.co.strato.portal.ml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDto.ListArg;
import kr.co.strato.portal.ml.service.MLInterfaceAPIAsyncService;

@RequestMapping("/api/v1/ml")
@Api(tags = {"ML 시작/종료/삭제 등. KETI에서 호출하는 API"})
@RestController
public class MLInterfaceAPIController {
	
	@Autowired
	private MLInterfaceAPIAsyncService apiService;

	/**
	 * ML Step 시작
	 * @param applyDto
	 */
	@Operation(summary = "ML Step 시작", description = "Machine learning 시작 API")
	@PostMapping("/apply")
	public ResponseWrapper<String> apply(@RequestBody MLDto.ApplyArg applyDto) {
		String mlId = apiService.apply(applyDto);
		return new ResponseWrapper<>(mlId);
	}
	
	/**
	 * ML Step 완료 시 호출(전처리, 검증, 학습, 추론
	 * @param mlId
	 */
	@Operation(summary = "ML Step 완료", description = "서비스 단계를 제외한 전처리, 검증, 학습, 추론 완료 시 호출 (KETI에서 호출)")
	@PutMapping("/finish/{mlId}")
	public ResponseWrapper<String> finish(@PathVariable("mlId") String mlId) {
		apiService.finish(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML Step 중지 및 삭제
	 */
	@Operation(summary = "ML Step 삭제", description = "Machine learning 중지 및 삭제")
	@DeleteMapping("/delete/{mlId}")
	public ResponseWrapper<String> delete(@PathVariable("mlId") String mlId) {
		apiService.delete(mlId);
		return new ResponseWrapper<>();
	}
	
	/**
	 * ML 리스트
	 * @param pageRequest
	 */
	@Operation(summary = "ML 리스트", description = "ML 리스트 요청")
	@PostMapping("/ml/list")
	public ResponseWrapper<Object> mlList(@RequestBody ListArg param) {
		return new ResponseWrapper<>(apiService.getMlList(param));
	}
	
	/**
	 * ML 상세 정보
	 * @param mlId
	 */
	@Operation(summary = "ML 상세", description = "ML 상세 정보")
	@GetMapping("/{mlId}")
	public ResponseWrapper<MLDto.Detail> mlDetail(@PathVariable("mlId") String mlId) {
		MLDto.Detail detail = apiService.getMl(mlId);
		return new ResponseWrapper<>(detail);
	}
}
