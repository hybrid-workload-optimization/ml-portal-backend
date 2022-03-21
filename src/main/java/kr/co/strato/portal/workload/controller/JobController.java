package kr.co.strato.portal.workload.controller;

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
import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.JobArgDto;
import kr.co.strato.portal.workload.model.JobDto;
import kr.co.strato.portal.workload.service.DeploymentService;
import kr.co.strato.portal.workload.service.JobService;

@RequestMapping("/api/v1/workload")
@RestController
public class JobController {

	@Autowired
	JobService jobService;
	

	@GetMapping("/jobs")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<JobDto>> getList(PageRequest pageRequest, JobArgDto args) {
		Page<JobDto> result = jobService.getList(pageRequest, args);
		return new ResponseWrapper<Page<JobDto>>(result);
	}
	
	@GetMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> get(@PathVariable(name = "idx") Long idx) {
		JobDto result = jobService.get(idx);
		return new ResponseWrapper<JobDto>(result);
	}
	
	@PostMapping("/jobs")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<JobDto> post(@RequestBody JobArgDto jobArgDto) {
		ResponseWrapper<JobDto> result = null;
		jobService.create(jobArgDto);
		return result;
	}
	
	@PutMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> put(@PathVariable(name = "idx") Long idx, @RequestBody JobArgDto jobArgDto) {
		jobArgDto.setJobIdx(idx);
		jobService.update( jobArgDto);
		return new ResponseWrapper<JobDto>();
	}
	
	@DeleteMapping("/jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<JobDto> delete(@PathVariable(name = "idx") Long idx, @RequestBody JobArgDto jobArgDto) {
		jobArgDto.setJobIdx(idx);
		jobService.delete(jobArgDto);
		return new ResponseWrapper<JobDto>();
	}
}
