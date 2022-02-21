package kr.co.strato.portal.workload.controller;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.service.StatefulSetManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StatefulSetManageController {

    @Autowired
    private StatefulSetManageService statefulSetService;

    @PostMapping("api/v1/statefulset-manage/statefulset")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<Long>> createStatefulSet(@Valid @RequestBody StatefulSetDto.ReqCreateDto reqCreateDto){
        List<Long> results = statefulSetService.createStatefulSet(reqCreateDto);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/statefulset-manage/staetfulset")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<StatefulSetDto.ResListDto>> getStatefulSetList(PageRequest pageRequest, StatefulSetDto.SearchParam searchParam){
        Page<StatefulSetDto.ResListDto> results = statefulSetService.getStatefulSetList(pageRequest.of(), searchParam);

        return new ResponseWrapper<>(results);
    }
}
