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

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterPersistentVolumeDto;
import kr.co.strato.portal.cluster.service.ClusterPersistentVolumeService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class ClusterPersistentVolumeController {

	@Autowired
	private ClusterPersistentVolumeService persistentVolumeService;

	
	/**
	 * @param kubeConfigId
	 * @return
	 * k8s data 호출 및 db저장
	 */
	@GetMapping("/api/v1/cluster/clusterPersistentVolumeListSet")
	@ResponseStatus(HttpStatus.OK)
	public List<PersistentVolume> getClusterPersistentVolumeListSet(@RequestParam Integer kubeConfigId) {
		if (kubeConfigId == null) {
			throw new BadRequestException("kubeConfigId id is null");
		}
		return persistentVolumeService.getClusterPersistentVolumeListSet(kubeConfigId);
	}
	
	
	/**
	 * @param pageRequest
	 * @return
	 * page List
	 */
	@GetMapping("/api/v1/cluster/clusterPersistentVolumes")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ClusterPersistentVolumeDto>> getClusterPersistentVolumeList(String name,PageRequest pageRequest){
        Page<ClusterPersistentVolumeDto> results = persistentVolumeService.getClusterPersistentVolumeList(name,pageRequest.of());
        return new ResponseWrapper<>(results);
    }


	
	@GetMapping("/api/v1/cluster/clusterPersistentVolumesYaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getClusterPersistentVolumeDetail(@RequestParam Integer kubeConfigId,String name) {
		String resBody = persistentVolumeService.getClusterPersistentVolumeYaml(kubeConfigId,name);

		return new ResponseWrapper<>(resBody);
	}
	
	@PostMapping("/api/v1/cluster/registerClusterPersistentVolume")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterPersistentVolume(@RequestBody YamlApplyParam yamlApplyParam ,@RequestParam Integer kubeConfigId) {
		List<Long> results = null;
		
		try {
			results = persistentVolumeService.registerClusterPersistentVolume(yamlApplyParam,kubeConfigId);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(results);
	}

	@DeleteMapping("/api/v1/cluster/deletClusterPersistentVolume")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterPersistentVolume(@RequestParam Integer kubeConfigId, 	@RequestParam PersistentVolumeEntity persistentVolumeEntity) {
		try {
			persistentVolumeService.deleteClusterPersistentVolume(kubeConfigId, persistentVolumeEntity);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(null);
	}
	
	@PutMapping("/api/v1/clusters/updateClusterPersistentVolume/{id}")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Long> updateClusterPersistentVolume(@PathVariable(required = true) Long id, @RequestBody YamlApplyParam yamlApplyParam){
        Long result = null;
        try {
        	persistentVolumeService.updateClusterPersistentVolume(id,yamlApplyParam);        	
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
        
        return new ResponseWrapper<>(result);
    }
	
	
	@GetMapping("/api/v1/cluster/clusterPersistentVolumes/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterPersistentVolumeDto> getClusterPersistentVolumeDetail(@PathVariable("id") Long id) {
		ClusterPersistentVolumeDto resBody = persistentVolumeService.getClusterPersistentVolumeDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	
}
