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

    @GetMapping("api/v1/select/projects/{projectIdx}/clusters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getClusters(@PathVariable Long projectIdx){
        List<SelectDto> results = selectService.getSelectClusters(projectIdx);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/select/clusters/{clusterIdx}/namespaces")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectDto>> getNamespaces(@PathVariable Long clusterIdx){
        List<SelectDto> results = selectService.getSelectNamespaces(clusterIdx);

        return new ResponseWrapper<>(results);
    }
}
