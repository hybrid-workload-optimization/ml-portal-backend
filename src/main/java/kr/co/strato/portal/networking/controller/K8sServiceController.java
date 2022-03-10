package kr.co.strato.portal.networking.controller;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class K8sServiceController {

    @PostMapping("api/v1/networking/services")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<K8sServiceDto.ResListDto>> getServices(PageRequest pageRequest, K8sServiceDto.SearchParam searchParam){

        return null;
    }

}
