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

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterStorageClassDto;
import kr.co.strato.portal.cluster.service.ClusterStorageClassService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class ClusterStorageClassController {

	@Autowired
	private ClusterStorageClassService storageClassService;

	
	/**
	 * @param kubeConfigId
	 * @return
	 * k8s data 호출 및 db저장
	 */
	@GetMapping("/api/v1/cluster/clusterStorageClassListSet")
	@ResponseStatus(HttpStatus.OK)
	public List<StorageClass> getClusterStorageClassListSet(@RequestParam Integer kubeConfigId) {
		if (kubeConfigId == null) {
			throw new BadRequestException("kubeConfigId id is null");
		}
		return storageClassService.getClusterStorageClassListSet(kubeConfigId);
	}
	
	
	/**
	 * @param pageRequest
	 * @return
	 * page List
	 */
	@GetMapping("/api/v1/cluster/clusterStorageClasss")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ClusterStorageClassDto>> getClusterStorageClassList(String name,PageRequest pageRequest){
        Page<ClusterStorageClassDto> results = storageClassService.getClusterStorageClassList(name,pageRequest.of());
        return new ResponseWrapper<>(results);
    }


	
	@GetMapping("/api/v1/cluster/clusterStorageClasssYaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getClusterStorageClassDetail(@RequestParam Integer kubeConfigId,String name) {
		String resBody = storageClassService.getClusterStorageClassYaml(kubeConfigId,name);

		return new ResponseWrapper<>(resBody);
	}
	
	@PostMapping("/api/v1/cluster/registerClusterStorageClass")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterStorageClass(@RequestBody YamlApplyParam yamlApplyParam ,@RequestParam Integer kubeConfigId) {
		List<Long> ids = null;
		
		try {
			 ids = storageClassService.registerClusterStorageClass(yamlApplyParam,kubeConfigId);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(ids);
	}

	@DeleteMapping("/api/v1/cluster/deletClusterStorageClass")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterStorageClass(@RequestParam Integer kubeConfigId, 	@RequestParam StorageClassEntity storageClassEntity) {
		try {
			storageClassService.deleteClusterStorageClass(kubeConfigId, storageClassEntity);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(null);
	}
	
	@PutMapping("/api/v1/clusters/updateClusterStorageClass/{id}")
    public ResponseWrapper<Long> updateClusterStorageClass(@PathVariable(required = true) Long id, @RequestBody YamlApplyParam yamlApplyParam){
        Long result = null;
        try {
        	//        	
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
        
        return new ResponseWrapper<>(result);
    }
	
	
	@GetMapping("/api/v1/cluster/clusterStorageClasss/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterStorageClassDto> getClusterStorageClassDetail(@PathVariable("id") Long id) {
		ClusterStorageClassDto resBody = storageClassService.getClusterStorageClassDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	
}
