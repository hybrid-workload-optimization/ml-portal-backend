package kr.co.strato.portal.workload.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.workload.v1.model.DeploymentArgDto;
import kr.co.strato.portal.workload.v1.model.DeploymentDto;
import kr.co.strato.portal.workload.v1.model.DeploymentArgDto.UpdateParam;
import kr.co.strato.portal.workload.v1.service.DeploymentOnlyApiService;

@RestController
@RequestMapping("/api/v1/workload/onlyapi")
public class DeploymentOnlyApiController extends CommonController {

	@Autowired
	DeploymentOnlyApiService deploymentService;

	@GetMapping("/deployment/list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<DeploymentDto>> getDeploymentList(PageRequest pageRequest, DeploymentArgDto.ListParam args) {
		Page<DeploymentDto> result = deploymentService.getList(pageRequest, args);
		return new ResponseWrapper<Page<DeploymentDto>>(result);
	}
	
	@GetMapping("/deployment")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> getDeployment(
			@RequestParam("clusterIdx") Long clusterIdx,
    		@RequestParam("namespace") String namespace,
    		@RequestParam("name") String name) {
		DeploymentDto result = deploymentService.get(clusterIdx, namespace, name);
		return new ResponseWrapper<DeploymentDto>(result);
	}
	
	@PostMapping("/deployment")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<DeploymentDto> postDeployment(@RequestBody DeploymentArgDto deploymentArgDto) {
		ResponseWrapper<DeploymentDto> result = null;
		deploymentService.save(deploymentArgDto);
		return result;
	}
	
	@PutMapping("/deployment")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> putDeployment(@RequestBody UpdateParam deploymentArgDto) {
		deploymentService.update(deploymentArgDto);
		return new ResponseWrapper<DeploymentDto>();
	}
	
	@DeleteMapping("/deployment")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<DeploymentDto> deleteDeployment(@RequestBody DeploymentArgDto.DeleteParam param) {
		deploymentService.delete(param.getClusterIdx(), param.getNamespace(), param.getName());
		return new ResponseWrapper<DeploymentDto>();
	}

	@GetMapping("/deployment/yaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getDeploymentYaml(
			@RequestParam("clusterIdx") Long clusterIdx,
    		@RequestParam("namespace") String namespace,
    		@RequestParam("name") String name) {
		String result = deploymentService.getYaml(clusterIdx, namespace, name);
		return new ResponseWrapper<String>(result);
	}
}
