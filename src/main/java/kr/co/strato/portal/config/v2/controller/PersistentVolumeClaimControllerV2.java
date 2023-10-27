package kr.co.strato.portal.config.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.config.v2.model.ConfigCommonDto;
import kr.co.strato.portal.config.v2.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.v2.service.PersistentVolumeClaimServiceV2;
import lombok.extern.slf4j.Slf4j;

@Api(tags = {"Config > PersistentVolumeClaim V2"})
@RequestMapping("/api/v2/config/persistentVolumeClaim")
@Slf4j
@RestController
public class PersistentVolumeClaimControllerV2 extends CommonController {

	@Autowired
	PersistentVolumeClaimServiceV2 pvcService;
	
	
	
	@GetMapping("/{clusterIdx}")
    public ResponseWrapper<List<PersistentVolumeClaimDto>> getPersistentVolumeClaimList(
    		@PathVariable(required = true) Long clusterIdx) throws Exception {
		List<PersistentVolumeClaimDto> list = pvcService.getList(clusterIdx);
        return new ResponseWrapper<>(list);
    }
	
	@PostMapping("")
    public ResponseWrapper<PersistentVolumeClaimDto> getPersistentVolumeClaim(
    		@RequestBody ConfigCommonDto.Search search) throws Exception {
		PersistentVolumeClaimDto result = pvcService.getDetail(search);
        return new ResponseWrapper<>(result);
    }
	
	@PostMapping("/yaml")
    public ResponseWrapper<String> getPersistentVolumeClaimYaml(
    		@RequestBody ConfigCommonDto.Search search) throws Exception  {
		String result = pvcService.getYaml(search);        
        return new ResponseWrapper<>(result);
    }
	
	@DeleteMapping("")
    public ResponseWrapper<Boolean> deletePersistentVolumeClaim(
    		@RequestBody ConfigCommonDto.Search search) throws Exception {
		boolean result = pvcService.delete(search); 
        return new ResponseWrapper<>(result);
    }
}
