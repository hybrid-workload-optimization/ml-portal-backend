package kr.co.strato.portal.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.model.SelectDto;
import kr.co.strato.portal.common.service.SelectService;

@RestController
public class SelectController extends CommonController {
    @Autowired
    private SelectService selectService;

    @GetMapping("api/v1/select/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getProjects(){
        List<SelectDto> results = selectService.getSelectProjects(getLoginUser());

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/select/namespaces")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getNamespaces(@RequestParam Long clusterIdx){
        List<SelectDto> results = selectService.getSelectNamespaces(clusterIdx);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/select/clusters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getClusters(@RequestParam(required = false) Long projectIdx){
        List<SelectDto> results = selectService.getSelectClusters(getLoginUser(), projectIdx);

        return new ResponseWrapper<>(results);
    }
    
    @GetMapping("api/v1/select/user/roles")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getUserRoles(){
        List<SelectDto> results = selectService.getUserRoles();

        return new ResponseWrapper<>(results);
    }
}
