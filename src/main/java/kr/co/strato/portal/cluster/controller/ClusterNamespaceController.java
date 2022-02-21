package kr.co.strato.portal.cluster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.fabric8.kubernetes.api.model.Namespace;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterNamespaceDto;
import kr.co.strato.portal.cluster.service.ClusterNamespaceService;



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
	public List<Namespace> getClusterNamespaceListSet(@RequestParam Integer kubeConfigId) {
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
    public ResponseWrapper<Page<ClusterNamespaceDto>> getClusterNamespaceList(String name,PageRequest pageRequest){
        Page<ClusterNamespaceDto> results = namespaceService.getClusterNamespaceList(name,pageRequest.of());
        return new ResponseWrapper<>(results);
    }


	
	@GetMapping("/api/v1/cluster/clusterNamespacesYaml/{name:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getClusterNamespaceDetail(@PathVariable("name") String name,@RequestParam Integer kubeConfigId) {
		String resBody = namespaceService.getClusterNamespaceYaml(kubeConfigId,name);

		return new ResponseWrapper<>(resBody);
	}
	
	@PostMapping("/api/v1/cluster/registerClusterNamespace")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterNamespace(@RequestBody YamlApplyParam yamlApplyParam ,@RequestParam Integer kubeConfigId) {
		List<Long> ids = null;
		
		try {
			 ids = namespaceService.registerClusterNamespace(yamlApplyParam,kubeConfigId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		return new ResponseWrapper<>(ids);
	}

	@DeleteMapping("/api/v1/cluster/deletClusterNamespace")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterNamespace(@RequestParam Integer kubeConfigId, 	@RequestParam NamespaceEntity namespaceEntity) {
		try {
			namespaceService.deleteClusterNamespace(kubeConfigId, namespaceEntity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		return new ResponseWrapper<>(null);
	}
	
	@GetMapping("/api/v1/cluster/clusterNamespaces/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterNamespaceDto> getClusterNamespaceDetail(@PathVariable("id") Long id) {
		ClusterNamespaceDto resBody = namespaceService.getClusterNamespaceDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	
}
