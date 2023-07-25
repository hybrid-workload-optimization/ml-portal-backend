package kr.co.strato.portal.workload.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.WorkloadDto;
import kr.co.strato.portal.workload.service.WorkloadService;

@RequestMapping("/api/v1/workload")
@RestController
public class WorkloadController {

	@Autowired
	private WorkloadService workloadService;	
	
	@ApiOperation(value="Workload 리스트 요청")
	@PostMapping("/list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<WorkloadDto.List>> getList(@RequestBody WorkloadDto.SearchParam param) {
		List<WorkloadDto.List> list = workloadService.getList(param);
		return new ResponseWrapper<List<WorkloadDto.List>>(list);
	}
}
