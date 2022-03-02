package kr.co.strato.portal.workload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.service.PodService;

@RestController
public class PodController {
    @Autowired
    private PodService podService;
    
    @PostMapping("api/v1/pods")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<PodDto.ResListDto>> getPodList(PageRequest pageRequest, PodDto.SearchParam searchParam) {
    	Page<PodDto.ResListDto> results = podService.getPods(pageRequest.of(), searchParam);

        return new ResponseWrapper<Page<PodDto.ResListDto>>(results);
    }
}
