package kr.co.strato.portal.workload.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.workload.v1.model.JobArgDto;
import kr.co.strato.portal.workload.v1.model.JobDto;
import kr.co.strato.portal.workload.v1.service.JobService;

@RequestMapping("/api/v1/workload")
@RestController
public class JobController extends CommonController {

	@Autowired
	JobService jobService;
	

	@GetMapping("/jobs")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<JobDto>> getJobList (PageRequest pageRequest, JobArgDto args) {
		Page<JobDto> result = jobService.getList(pageRequest, args);
		return new ResponseWrapper<Page<JobDto>>(result);
	}
	
	@GetMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> getJob (@PathVariable(name = "idx") Long idx) {
		JobDto result = jobService.get(idx, getLoginUser());
		return new ResponseWrapper<JobDto>(result);
	}
	
	@GetMapping("/jobs/{idx}/yaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getJobYaml (@PathVariable(name = "idx") Long idx) {
		String result = jobService.getYaml(idx);
		return new ResponseWrapper<String>(result);
	}
	
	@PostMapping("/jobs")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<JobDto> postJob (@RequestBody JobArgDto jobArgDto) {
		ResponseWrapper<JobDto> result = null;
		jobService.create(jobArgDto);
		return result;
	}
	
	@PutMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> putJob (@PathVariable(name = "idx") Long idx, @RequestBody JobArgDto jobArgDto) {
		jobArgDto.setJobIdx(idx);
		jobService.update( jobArgDto);
		return new ResponseWrapper<JobDto>();
	}
	
	@DeleteMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> deleteJob (@PathVariable(name = "idx") Long idx, @RequestBody JobArgDto jobArgDto) {
		jobArgDto.setJobIdx(idx);
		jobService.delete(jobArgDto);
		return new ResponseWrapper<JobDto>();
	}
}
