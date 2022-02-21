package kr.co.strato.portal.workload.controller;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.service.StatefulSetDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class StatefulSetDetailController {
    @Autowired
    private StatefulSetDetailService statefulSetDetailService;


    @DeleteMapping("api/v1/statefulset-manage/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteStatefulSet(@PathVariable Integer id){
        boolean isDeleted = statefulSetDetailService.deleteStatefulSet(id);

        return new ResponseWrapper<>(isDeleted);
    }

    @PatchMapping("api/v1/statefulset-manage/statefulsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<Long>> updateStatefulSet(@PathVariable Integer id, @RequestBody StatefulSetDetailDto.ReqUpdateDto reqUpdateDto){

        return null;
    }
}
