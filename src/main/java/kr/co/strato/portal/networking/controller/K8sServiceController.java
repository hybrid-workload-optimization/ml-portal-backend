package kr.co.strato.portal.networking.controller;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import kr.co.strato.portal.networking.service.K8sServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class K8sServiceController {

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

        return null;
    }
}
