package kr.co.strato.portal.workload.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.CommonType;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.service.DeploymentService;

@RestController
@RequestMapping("/api/v1/workload")
public class DeploymentController {

	@Autowired
	DeploymentService deploymentService;

	@GetMapping("/deployments")
	public ResponseWrapper<List<DeploymentDto>> getList() {
		ResponseWrapper<List<DeploymentDto>> result = null;
		List<DeploymentDto> deployments = deploymentService.getList();
		if(deployments != null && !deployments.isEmpty()) 
			result = new ResponseWrapper<List<DeploymentDto>>(deployments);
		else
			result = new ResponseWrapper<List<DeploymentDto>>(CommonType.OK);
		
		return result;
	}
	
	@GetMapping("/deployments/{idx}")
	public ResponseWrapper<DeploymentDto> get(@PathVariable(name = "idx") Long idx) {
		ResponseWrapper<DeploymentDto> result = null;
		
		DeploymentDto deploymentDto = deploymentService.get(idx);
		if(deploymentDto != null) 
			result = new ResponseWrapper<DeploymentDto>(deploymentDto);
		else 
			result = new ResponseWrapper<DeploymentDto>(CommonType.OK);
		
		return result;
	}
	
	@PostMapping("/deployments")
	public ResponseWrapper<DeploymentDto> post(@RequestBody DeploymentDto deploymentDto) {
		ResponseWrapper<DeploymentDto> result = null;
		deploymentService.save(deploymentDto);
		
		result = new ResponseWrapper<DeploymentDto>(CommonType.OK);
		return result;
	}
	
	@PutMapping("/deployments/{idx}")
	public ResponseWrapper<DeploymentDto> put(@PathVariable(name = "idx") Long idx, @RequestBody DeploymentDto deploymentDto) {
		deploymentDto.setIdx(idx);
		
		ResponseWrapper<DeploymentDto> result = null;
		deploymentService.save(deploymentDto);
		
		result = new ResponseWrapper<DeploymentDto>(CommonType.OK);
		return result;
	}
	
	@DeleteMapping("/deployments/{idx}")
	public ResponseWrapper<DeploymentDto> delete(@PathVariable(name = "idx") Long idx) {
		ResponseWrapper<DeploymentDto> result = null;
		deploymentService.delete(idx);
		
		result = new ResponseWrapper<DeploymentDto>(CommonType.OK);
		return result;
	}
}
