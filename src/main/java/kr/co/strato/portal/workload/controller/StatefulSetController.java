package kr.co.strato.portal.workload.controller;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.service.StatefulSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StatefulSetController {

    @Autowired
    private StatefulSetService statefulSetService;

    @PostMapping("/api/v1/workload/statefulsets")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<List<Long>> createStatefulSet(@Valid @RequestBody StatefulSetDto.ReqCreateDto reqCreateDto){
        List<Long> results = statefulSetService.createStatefulSet(reqCreateDto);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("/api/v1/workload/statefulsets")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<StatefulSetDto.ResListDto>> getStatefulSetList(PageRequest pageRequest, StatefulSetDto.SearchParam searchParam){
        Page<StatefulSetDto.ResListDto> results = statefulSetService.getStatefulSets(pageRequest.of(), searchParam);

        return new ResponseWrapper<>(results);
    }

    @DeleteMapping("api/v1/workload/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteStatefulSet(@PathVariable Long id){
        boolean isDeleted = statefulSetService.deleteStatefulSet(id);

        return new ResponseWrapper<>(isDeleted);
    }

    @PutMapping("api/v1/workload/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updateStatefulSet(@PathVariable Long id, @RequestBody StatefulSetDetailDto.ReqUpdateDto reqUpdateDto){
        List<Long> results = statefulSetService.updateStatefulSet(id, reqUpdateDto);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/workload/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<StatefulSetDetailDto.ResDetailDto> getStatefulSet(@PathVariable Long id){
        StatefulSetDetailDto.ResDetailDto result = statefulSetService.getStatefulSet(id);

        return new ResponseWrapper<>(result);
    }

    @GetMapping("api/v1/workload/statefulsets/{id}/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getStaefulSetYaml(@PathVariable Long id){

        String result = statefulSetService.getStatefulSetYaml(id);

        return new ResponseWrapper<>(result);
    }
}
