package kr.co.strato.portal.cluster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.fabric8.kubernetes.api.model.Namespace;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.service.ClusterNamespaceService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class ClusterNamespaceController {

	@Autowired
	private ClusterNamespaceService namespaceService;

	
	/**
	 * @param kubeConfigId
	 * @return
	 * k8s data 호출 및 db저장
	 */
	@GetMapping("/api/v1/cluster/clusterNamespaceListSet")
	@ResponseStatus(HttpStatus.OK)
	public List<Namespace> getClusterNamespaceListSet(@RequestParam Long kubeConfigId) {
		if (kubeConfigId == null) {
			throw new BadRequestException("kubeConfigId id is null");
		}
		return namespaceService.getClusterNamespaceListSet(kubeConfigId);
	}
	
	
	/**
	 * @param pageRequest
	 * @return
	 * page List
	 */
	@GetMapping("/api/v1/cluster/clusterNamespaces")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ClusterNamespaceDto.ResListDto>> getClusterNamespaceList(PageRequest pageRequest, ClusterNamespaceDto.SearchParam searchParam){
        Page<ClusterNamespaceDto.ResListDto> results = namespaceService.getClusterNamespaceList(pageRequest.of(), searchParam);
        return new ResponseWrapper<>(results);
    }

	@GetMapping("api/v1/cluster/clusterNamespaces/{id}/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getNamespaceYaml(@PathVariable Long id){
        String result = namespaceService.getYaml(id);

        return new ResponseWrapper<>(result);
    }

	
	@GetMapping("/api/v1/cluster/clusterNamespacesYaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getClusterNamespaceDetail(@RequestParam Long kubeConfigId,String name) {
		String resBody = namespaceService.getClusterNamespaceYaml(kubeConfigId,name);

		return new ResponseWrapper<>(resBody);
	}
	
	@PostMapping("/api/v1/cluster/registerClusterNamespace")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterNamespace(@RequestBody ClusterNamespaceDto.ReqCreateDto yamlApplyParam) {
		List<Long> ids = null;
		
		try {
			 ids = namespaceService.registerClusterNamespace(yamlApplyParam);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(ids);
	}

	@DeleteMapping("/api/v1/cluster/deletClusterNamespace/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterNamespace(@PathVariable Long id) {
		boolean isDeleted = namespaceService.deleteClusterNamespace(id);
		
		return new ResponseWrapper<>(isDeleted);
	}
	
	@PutMapping("/api/v1/cluster/updateClusterNamespace/{id}")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Long> updateClusterNamespace(@PathVariable(required = true) Long id, @RequestBody YamlApplyParam yamlApplyParam){
        Long result = null;
        try {
        	namespaceService.updateClusterNamespace(id,yamlApplyParam); 
        	result = id;
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
        
        return new ResponseWrapper<>(result);
    }
	
	
	@GetMapping("/api/v1/cluster/clusterNamespaces/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterNamespaceDto.ResDetailDto> getClusterNamespaceDetail(@PathVariable("id") Long id) {
		ClusterNamespaceDto.ResDetailDto resBody = namespaceService.getClusterNamespaceDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	
}
