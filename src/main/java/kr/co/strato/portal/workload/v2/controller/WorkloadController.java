package kr.co.strato.portal.workload.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.v2.model.WorkloadDto;
import kr.co.strato.portal.workload.v2.service.WorkloadServiceV2;

@Api(tags = {"Workload API"})
@RequestMapping("/api/v1/workload")
@RestController
public class WorkloadController {

	@Autowired
	private WorkloadServiceV2 workloadService;	
	
	@ApiOperation(value="Workload 리소스 리스트 조회")
	@PostMapping("/list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<WorkloadDto.List>> getList(@RequestBody WorkloadDto.SearchParam param) {
		List<WorkloadDto.List> list = workloadService.getList(param);
		return new ResponseWrapper<List<WorkloadDto.List>>(list);
	}
	
	@ApiOperation(value="Workload 리소스 상세 조회")
	@PostMapping("/detail")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Object> getDetail(@RequestBody WorkloadDto.DetailParam param) {
		Object result = workloadService.getDetail(param);
		return new ResponseWrapper<Object>(result);
	}
	
	@ApiOperation(value="Workload 생성 및 업데이트")
	@PostMapping("/apply")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<WorkloadDto.List>> apply(@RequestBody WorkloadDto.ApplyDto param) {
		List<WorkloadDto.List> list = workloadService.apply(param);
		return new ResponseWrapper<List<WorkloadDto.List>>(list);
	}	
}
