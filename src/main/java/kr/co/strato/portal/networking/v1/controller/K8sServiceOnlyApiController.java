package kr.co.strato.portal.networking.v1.controller;

import java.util.List;

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
import kr.co.strato.portal.networking.v1.model.K8sServiceOnlyApiDto;
import kr.co.strato.portal.networking.v1.service.K8sServiceOnlyApiService;

@RestController
@RequestMapping("/api/v1/networking/onlyapi")
public class K8sServiceOnlyApiController extends CommonController {

    @Autowired
    private K8sServiceOnlyApiService k8sServiceService;

    @GetMapping("/services/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<K8sServiceOnlyApiDto.ResListDto>> getServices(PageRequest pageRequest, K8sServiceOnlyApiDto.SearchParam searchParam){
        Page<K8sServiceOnlyApiDto.ResListDto> results = k8sServiceService.getServices(pageRequest.of(), searchParam);

        return new ResponseWrapper<>(results);
    }

    @PostMapping("/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<Long>> createService(@RequestBody K8sServiceOnlyApiDto.ReqCreateDto reqCreateDto) {
        k8sServiceService.createService(reqCreateDto);
        return new ResponseWrapper<>();
    }

    @PutMapping("/services")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updateService(@RequestBody K8sServiceOnlyApiDto.ReqCreateDto reqCreateDto) {
        k8sServiceService.updateService(reqCreateDto);
        return new ResponseWrapper<>();
    }

    @DeleteMapping("/services")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteService(@RequestBody K8sServiceOnlyApiDto.DeleteParam param){
        boolean isDeleted = k8sServiceService.deleteService(param.getClusterIdx(), param.getNamespace(), param.getName());
        return new ResponseWrapper<>(isDeleted);
    }

    @GetMapping("/services/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getServiceYaml(@RequestBody K8sServiceOnlyApiDto.SearchParam param){
        String result = k8sServiceService.getServiceYaml(param.getClusterIdx(), param.getNamespace(), param.getName());

        return new ResponseWrapper<>(result);
    }

    @GetMapping("/services")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<K8sServiceOnlyApiDto.ResDetailDto> getService(
    		@RequestParam Long clusterIdx, 
    		@RequestParam String namespace,
    		@RequestParam String name){
        K8sServiceOnlyApiDto.ResDetailDto result = k8sServiceService.getService(clusterIdx, namespace, name);
        return new ResponseWrapper<>(result);
    }
}
