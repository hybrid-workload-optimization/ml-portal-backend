package kr.co.strato.portal.networking.service;


import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.service.ServiceDomainService;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import kr.co.strato.portal.networking.model.K8sServiceDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@org.springframework.stereotype.Service
public class K8sServiceService {
    @Autowired
    private ServiceDomainService serviceDomainService;

    public Page<K8sServiceDto.ResListDto> getServices(Pageable pageable, K8sServiceDto.SearchParam searchParam){
        Page<ServiceEntity> serviceEntities = serviceDomainService.getServices(
                pageable,
                searchParam.getProjectIdx(),
                searchParam.getClusterIdx(),
                searchParam.getNamespaceIdx());

//        List<K8sServiceDto.ResListDto> dtos = serviceEntities.stream().map(e ->
//                K8sServiceDtoMapper.IN)

        return null;
    }
}
