package kr.co.strato.portal.workload.v1.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.config.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.workload.v1.model.PodDto;
import kr.co.strato.portal.workload.v1.service.PodService;

@RestController
public class PodController extends CommonController {
    @Autowired
    private PodService podService;
    
    @GetMapping("api/v1/pods")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<PodDto.ResListDto>> getPodList(PageRequest pageRequest, PodDto.SearchParam searchParam) {
    	Page<PodDto.ResListDto> results = podService.getPods(pageRequest.of(), searchParam);

        return new ResponseWrapper<Page<PodDto.ResListDto>>(results);
    }
    
    @PostMapping("api/v1/pods")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<Long>> createPod(@Valid @RequestBody PodDto.ReqCreateDto reqCreateDto){
        List<Long> results = podService.createPod(reqCreateDto);

        return new ResponseWrapper<>(results);
    }
    
    @GetMapping("api/v1/pod/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<PodDto.ResDetailDto> getPodDetail(@PathVariable Long id){
        PodDto.ResDetailDto result = podService.getPodDetail(id, getLoginUser());

        return new ResponseWrapper<>(result);
    }
    
    @PatchMapping("api/v1/pod/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updatePod(@PathVariable Long id, @Valid @RequestBody PodDto.ReqUpdateDto reqUpdateDto){
        List<Long> results = podService.updatePod(id, reqUpdateDto);

        return new ResponseWrapper<>(results);
    }
    
    @DeleteMapping("api/v1/pod/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deletePod(@PathVariable Long id){
    	Boolean result = podService.deletePod(id);
        return new ResponseWrapper<>(result);
    }
    
    @GetMapping("api/v1/pod/ownerInfo/{podId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<PodDto.ResOwnerDto> getPodOwnerInfo(@PathVariable Long podId, @RequestParam("resourceType") String resourceType) {
    	PodDto.ResOwnerDto result = podService.getPodOwnerInfo(podId, resourceType);
    	return new ResponseWrapper<>(result);
    }
    
    @PostMapping("api/v1/pod/owner/podList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<PodDto.ResListDto>> getPodOwnerPodList(Long clusterId, @RequestBody PodDto.OwnerSearchParam searchParam) {
    	List<PodDto.ResListDto> result = podService.getPodOwnerPodList(clusterId, searchParam);
    	return new ResponseWrapper<>(result);
    }
    
    @GetMapping("api/v1/pod/log/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> getPodLogDownload(@RequestParam("clusterId") Long clusterId, @RequestParam("namespace") String namespace, @RequestParam("name") String name) {
    	ResponseEntity<ByteArrayResource> result = podService.getLogDownloadFile(clusterId, namespace, name);	
    	return result;

    }
    
    @GetMapping("api/v1/pod/{id}/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getPodYaml(@PathVariable Long id) {
    	String result = podService.getPodtYaml(id);
        return new ResponseWrapper<>(result);
    }
    
    @GetMapping("api/v1/pod/{podId}/pvcList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<PersistentVolumeClaimDto.ResListDto>> getPodPersistentVolumeClaim(@PathVariable Long podId){
    	List<PersistentVolumeClaimDto.ResListDto> result = podService.getPodPersistentVolumeClaim(podId);
        return new ResponseWrapper<>(result);
    }
}
