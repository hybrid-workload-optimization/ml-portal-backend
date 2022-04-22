package kr.co.strato.portal.networking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import kr.co.strato.portal.networking.service.K8sServiceService;

@RestController
public class K8sServiceController extends CommonController {

    @Autowired
    private K8sServiceService k8sServiceService;

    @GetMapping("api/v1/networking/services")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<K8sServiceDto.ResListDto>> getServices(PageRequest pageRequest, K8sServiceDto.SearchParam searchParam){
        Page<K8sServiceDto.ResListDto> results = k8sServiceService.getServices(pageRequest.of(), searchParam);

        return new ResponseWrapper<>(results);
    }

    @PostMapping("api/v1/networking/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<Long>> createService(@Valid @RequestBody K8sServiceDto.ReqCreateDto reqCreateDto){
        List<Long> results = k8sServiceService.createService(reqCreateDto);

        return new ResponseWrapper<>(results);
    }

    @PutMapping("api/v1/networking/services/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updateService(@PathVariable Long id, @RequestBody K8sServiceDto.ReqUpdateDto reqUpdateDto){
        List<Long> results = k8sServiceService.updateService(id, reqUpdateDto);

        return new ResponseWrapper<>(results);
    }

    @DeleteMapping("api/v1/networking/services/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteService(@PathVariable Long id){
        boolean isDeleted = k8sServiceService.deleteService(id);

        return new ResponseWrapper<>(isDeleted);
    }

    @GetMapping("api/v1/networking/services/{id}/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getServiceYaml(@PathVariable Long id){
        String result = k8sServiceService.getServiceYaml(id);

        return new ResponseWrapper<>(result);
    }

    @GetMapping("api/v1/networking/services/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<K8sServiceDto.ResDetailDto> getService(@PathVariable Long id){
        K8sServiceDto.ResDetailDto result = k8sServiceService.getService(id, getLoginUser());

        return new ResponseWrapper<>(result);
    }
}
