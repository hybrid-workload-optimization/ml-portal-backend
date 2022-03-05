package kr.co.strato.portal.common.controller;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.model.SelectDto;
import kr.co.strato.portal.common.service.SelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SelectController {
    @Autowired
    private SelectService selectService;

    @GetMapping("api/v1/select/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getProjects(){
        List<SelectDto> results = selectService.getSelectProjects();

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
        List<SelectDto> results = selectService.getSelectClusters(projectIdx);

        return new ResponseWrapper<>(results);
    }
}
