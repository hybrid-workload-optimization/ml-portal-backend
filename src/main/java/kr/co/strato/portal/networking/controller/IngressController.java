package kr.co.strato.portal.networking.controller;

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

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterPersistentVolumeDto;
import kr.co.strato.portal.networking.model.IngressDto;
import kr.co.strato.portal.networking.service.IngressService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class IngressController {

	@Autowired
	private IngressService ingressService;

	
	/**
	 * @param kubeConfigId
	 * @return
	 * k8s data 호출 및 db저장
	 */
	@GetMapping("/api/v1/networking/ingressListSet")
	@ResponseStatus(HttpStatus.OK)
	public List<Ingress> getIngressListSet(@RequestParam Long kubeConfigId) {
		if (kubeConfigId == null) {
			throw new BadRequestException("kubeConfigId id is null");
		}
		return ingressService.getIngressListSet(kubeConfigId);
	}
	
	
	/**
	 * @param pageRequest
	 * @return
	 * page List
	 */
	@GetMapping("/api/v1/networking/ingress")
	@ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<IngressDto.ResListDto>> getIngressList(PageRequest pageRequest,IngressDto.SearchParam searchParam){
        Page<IngressDto.ResListDto> results = ingressService.getIngressList(pageRequest.of(),searchParam);
        return new ResponseWrapper<>(results);
    }


	
	@GetMapping("/api/v1/networking/ingresssYaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getIngressDetail(@RequestParam Long kubeConfigId,String name,String namespace) {
		String resBody = ingressService.getIngressYaml(kubeConfigId,name,namespace);

		return new ResponseWrapper<>(resBody);
	}
	
	@PostMapping("/api/v1/networking/registerIngress")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerIngress(@RequestBody IngressDto.ReqCreateDto yamlApplyParam) {
		List<Long> ids = null;
		
		try {
			 ids = ingressService.registerIngress(yamlApplyParam);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(ids);
	}

	@DeleteMapping("/api/v1/networking/deletIngress/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteIngress(@PathVariable Long id, @RequestBody Long kubeConfigId) {
		boolean isDeleted = ingressService.deleteIngress(id,kubeConfigId);
		
		return new ResponseWrapper<>(isDeleted);
	}
	
	@PutMapping("/api/v1/networking/updateIngress/{id}")
    public ResponseWrapper<Long> updateIngress(@PathVariable Long id, @RequestBody YamlApplyParam yamlApplyParam){
        Long result = null;
        try {
        	ingressService.updateIngress(id, yamlApplyParam);  
        	result = id;
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
        
        return new ResponseWrapper<>(result);
    }
	
	
	@GetMapping("/api/v1/networking/ingress/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<IngressDto.ResDetailDto> getIngressDetail(@PathVariable("id") Long id) {
		IngressDto.ResDetailDto resBody = ingressService.getIngressDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	
}
