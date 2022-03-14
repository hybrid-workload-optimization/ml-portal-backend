package kr.co.strato.portal.workload.controller;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.service.StatefulSetDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatefulSetDetailController {
    @Autowired
    private StatefulSetDetailService statefulSetDetailService;


    @DeleteMapping("api/v1/statefulset-detail/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteStatefulSet(@PathVariable Long id){
        boolean isDeleted = statefulSetDetailService.deleteStatefulSet(id);

        return new ResponseWrapper<>(isDeleted);
    }

    @PatchMapping("api/v1/statefulset-detail/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updateStatefulSet(@PathVariable Long id, @RequestBody StatefulSetDetailDto.ReqUpdateDto reqUpdateDto){
        List<Long> results = statefulSetDetailService.updateStatefulSet(id, reqUpdateDto);

        return new ResponseWrapper<>(results);
    }

    @GetMapping("api/v1/statefulset-detail/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<StatefulSetDetailDto.ResDetailDto> getStatefulSet(@PathVariable Long id){
        StatefulSetDetailDto.ResDetailDto result = statefulSetDetailService.getStatefulSet(id);

        return new ResponseWrapper<>(result);
    }

    @GetMapping("api/v1/statefulset-detail/statefulsets/{id}/yaml")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<String> getStaefulSetYaml(@PathVariable Long id){

        String result = statefulSetDetailService.getStatefulSetYaml(id);

        return new ResponseWrapper<>(result);
    }
}
