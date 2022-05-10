package kr.co.strato.portal.workload.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.model.PodDto.ApiOwnerSearchParam;
import kr.co.strato.portal.workload.model.PodDto.ApiReqUpdateDto;
import kr.co.strato.portal.workload.model.PodDto.ApiSearchParam;
import kr.co.strato.portal.workload.service.PodOnlyApiService;

@RestController
public class PodOnlyApiController extends CommonController {
    @Autowired
    private PodOnlyApiService podService;
    
    @GetMapping("api/v1/pods/api/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<PodDto.ResListDto>> getPodList(PageRequest pageRequest, PodDto.SearchParam searchParam) {
    	Page<PodDto.ResListDto> results = podService.getPods(pageRequest.of(), searchParam);

        return new ResponseWrapper<Page<PodDto.ResListDto>>(results);
    }
    
    @PostMapping("api/v1/pods/api")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<String>> createPod(@RequestBody PodDto.ReqCreateDto reqCreateDto){
        List<String> results = podService.createPod(reqCreateDto);
        return new ResponseWrapper<>(results);
    }
    
    @GetMapping("api/v1/pod/api")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<PodDto.ResDetailDto> getPodDetail(
    		@RequestParam("clusterIdx") Long clusterIdx,
    		@RequestParam("namespace") String namespace,
    		@RequestParam("podName") String podName) {
        PodDto.ResDetailDto result = podService.getPodDetail(clusterIdx, namespace, podName, getLoginUser());

        return new ResponseWrapper<>(result);
    }
    
    @PutMapping("api/v1/pod/api")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<String>> updatePod(@RequestBody ApiReqUpdateDto updateDto){
        List<String> results = podService.updatePod(updateDto);

        return new ResponseWrapper<>(results);
    }
    
    @DeleteMapping("api/v1/pod/api")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deletePod(@RequestBody ApiSearchParam param) {
    	Boolean result = podService.deletePod(param.getClusterId(), param.getNamespace(), param.getPodName());
        return new ResponseWrapper<>(result);
    }
    
    @GetMapping("api/v1/pod/api/log/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> getPodLogDownload (
    		@RequestParam("clusterId") Long clusterId, 
    		@RequestParam("namespace") String namespace, 
    		@RequestParam("name") String name) {
    	
    	ResponseEntity<ByteArrayResource> result = podService.getLogDownloadFile(clusterId, namespace, name);	
    	return result;
    }
    
    @PostMapping("api/v1/pod/api/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getPodYaml(@RequestBody ApiSearchParam param) {
    	String result = podService.getPodtYaml(param.getClusterId(), param.getNamespace(), param.getPodName());
        return new ResponseWrapper<>(result);
    }
    
    @PostMapping("api/v1/pod/api/owner/podList")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<PodDto.ResListDto>> getPodListByOwner(@RequestBody ApiOwnerSearchParam searchParam) {
    	List<PodDto.ResListDto> result = podService.getPodListByOwner(searchParam);
    	return new ResponseWrapper<>(result);
    }
}
