package kr.co.strato.portal.common.controller;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.model.SelectProjectDto;
import kr.co.strato.portal.common.service.SelectService;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SelectController {
    @Autowired
    private SelectService selectService;

    @GetMapping("api/v1/select/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<SelectProjectDto>> getProjects(){
        List<SelectProjectDto> results = selectService.getSelectProjects();

        return new ResponseWrapper<>(results);
    }
}
