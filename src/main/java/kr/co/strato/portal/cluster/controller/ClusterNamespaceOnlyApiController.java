package kr.co.strato.portal.cluster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.service.ClusterNamespaceOnlyApiService;


@RestController
@RequestMapping("/api/v1/cluster/onlyapi")
public class ClusterNamespaceOnlyApiController {

	@Autowired
	private ClusterNamespaceOnlyApiService namespaceService;

	
	/**
	 * @param pageRequest
	 * @return
	 * page List
	 */
	@GetMapping("/clusterNamespaces/list")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ClusterNamespaceDto.ResListDto>> getClusterNamespaceList(PageRequest pageRequest, ClusterNamespaceDto.SearchParam searchParam){
        Page<ClusterNamespaceDto.ResListDto> results = namespaceService.getClusterNamespaceList(pageRequest.of(), searchParam);
        return new ResponseWrapper<>(results);
    }

	@GetMapping("/clusterNamespaces/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getNamespaceYaml(
    		@RequestParam("clusterIdx") Long clusterIdx,
    		@RequestParam("name") String name) {
        String result = namespaceService.getClusterNamespaceYaml(clusterIdx, name);
        return new ResponseWrapper<>(result);
    }
	
	@PostMapping("/registerClusterNamespace")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterNamespace(@RequestBody ClusterNamespaceDto.ReqCreateDto yamlApplyParam) {
		namespaceService.registerClusterNamespace(yamlApplyParam);
		return new ResponseWrapper<>();
	}

	@DeleteMapping("/deletClusterNamespace")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterNamespace(@RequestBody ClusterNamespaceDto.DeleteParam param) {
		boolean isDeleted = namespaceService.deleteClusterNamespace(param.getClusterIdx(), param.getName());
		
		return new ResponseWrapper<>(isDeleted);
	}
	
	@PutMapping("/updateClusterNamespace")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Long> updateClusterNamespace(@RequestBody ClusterNamespaceDto.ReqCreateDto yamlApplyParam){
		namespaceService.updateClusterNamespace(yamlApplyParam); 
		return new ResponseWrapper<>();
    }
	
	
	@GetMapping("/clusterNamespaces")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterNamespaceDto.ResDetailDto> getClusterNamespaceDetail(
			@RequestParam("clusterIdx") Long clusterIdx,
    		@RequestParam("name") String name) {
		ClusterNamespaceDto.ResDetailDto resBody = namespaceService.getClusterNamespaceDetail(clusterIdx, name);
		return new ResponseWrapper<>(resBody);
	}
}
