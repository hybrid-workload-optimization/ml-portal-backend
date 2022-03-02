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
import kr.co.strato.portal.workload.service.DeploymentService;

@RestController
@RequestMapping("/api/v1/workload")
public class DeploymentController {

	@Autowired
	DeploymentService deploymentService;

	@GetMapping("/deployments")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<DeploymentDto>> getList(PageRequest pageRequest, DeploymentArgDto args) {
		Page<DeploymentDto> result = deploymentService.getList(pageRequest, args);
		return new ResponseWrapper<Page<DeploymentDto>>(result);
	}
	
	@GetMapping("/deployments/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> get(@PathVariable(name = "idx") Long idx) {
		DeploymentDto result = deploymentService.get(idx);
		return new ResponseWrapper<DeploymentDto>(result);
	}
	
	@PostMapping("/deployments")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<DeploymentDto> post(@RequestBody DeploymentArgDto deploymentArgDto) {
		ResponseWrapper<DeploymentDto> result = null;
		deploymentService.create(deploymentArgDto);
		return result;
	}
	
	@PutMapping("/deployments/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> put(@PathVariable(name = "idx") Long idx, @RequestBody DeploymentArgDto deploymentArgDto) {
		deploymentArgDto.setDeploymentIdx(idx);
		deploymentService.update(deploymentArgDto);
		return new ResponseWrapper<DeploymentDto>();
	}
	
	@DeleteMapping("/deployments/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> delete(@PathVariable(name = "idx") Long idx) {
		deploymentService.delete(idx);
		return new ResponseWrapper<DeploymentDto>();
	}
}
