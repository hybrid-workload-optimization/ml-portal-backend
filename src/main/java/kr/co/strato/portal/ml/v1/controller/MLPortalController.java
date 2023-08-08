package kr.co.strato.portal.ml.v1.controller;

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
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.ml.v1.model.MLDto;
import kr.co.strato.portal.ml.v1.model.MLDto.ListArg;
import kr.co.strato.portal.ml.v1.service.MLInterfaceAPIAsyncService;
import kr.co.strato.portal.ml.v1.service.MLPortalService;
import kr.co.strato.portal.setting.model.UserDto;

@RequestMapping("/api/v1/ml/portal")
@Api(tags = {"ML Portal UI API(리스트/상세 등.)"})
@RestController
public class MLPortalController extends CommonController {
	
	@Autowired
	private MLPortalService apiService;
	
	@Autowired
	private MLInterfaceAPIAsyncService intefaceApiService;
	
	/**
	 * ML 리스트
	 * @param pageRequest
	 */
	@Operation(summary = "ML 리스트", description = "ML 리스트 요청")
	@PostMapping("/ml/list")
	public ResponseWrapper<Object> getMlList(@RequestBody ListArg param) {
		return new ResponseWrapper<>(apiService.getMlList(param));
	}
	
	/**
	 * ML 상세 정보
	 * @param mlId
	 */
	@Operation(summary = "ML 상세", description = "ML 상세 정보")
	@GetMapping("/{mlId}")
	public ResponseWrapper<MLDto.DetailForPortal> getMlDetail(@PathVariable("mlId") String mlId) {
		MLDto.DetailForPortal detail = apiService.getMl(mlId);
		return new ResponseWrapper<>(detail);
	}	
	
	/**
	 * ML 리소스 삭제
	 * @param mlId
	 */
	@Operation(summary = "ML 리소스 삭제", description = "ML 개별 리소스 삭제")
	@DeleteMapping("resource/{id}")
	public ResponseWrapper<Boolean> deleteResource(@PathVariable("id") Long resId) {
		boolean isDelete = apiService.deleteResource(resId);
		return new ResponseWrapper<>(isDelete);
	}
	
	@Operation(summary = "ML 생성", description = "Machine learning 시작 API") 
	@PostMapping("/registry")
	public ResponseWrapper<MLEntity> create(@RequestBody MLDto.ApplyArg applyDto) {
		UserDto user = getLoginUser();
		applyDto.setUserId(user.getUserId());
		MLEntity entity = intefaceApiService.apply(applyDto);
		return new ResponseWrapper<>(entity);
	}
	
	/**
	 * ML Step 중지 및 삭제
	 */
	@Operation(summary = "ML 삭제", description = "Machine learning 중지 및 삭제")
	@DeleteMapping("/delete")
	public ResponseWrapper<String> delete(@RequestBody MLDto.DeleteArg deleteArg) {
		intefaceApiService.delete(deleteArg);
		return new ResponseWrapper<>();
	}
	
}
