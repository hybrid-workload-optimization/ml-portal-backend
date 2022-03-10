package kr.co.strato.portal.networking.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.model.ServiceType;
import kr.co.strato.domain.service.service.ServiceDomainService;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import kr.co.strato.portal.networking.model.K8sServiceDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class K8sServiceService {
    @Autowired
    private ServiceDomainService serviceDomainService;

    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private ServiceAdapterService serviceAdapterService;

    public Page<K8sServiceDto.ResListDto> getServices(Pageable pageable, K8sServiceDto.SearchParam searchParam){
        Page<ServiceEntity> serviceEntities = serviceDomainService.getServices(
                pageable,
                searchParam.getProjectIdx(),
                searchParam.getClusterIdx(),
                searchParam.getNamespaceIdx());

        List<K8sServiceDto.ResListDto> dtos = serviceEntities.stream().map(e ->
                K8sServiceDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());

        Page<K8sServiceDto.ResListDto> page = new PageImpl<>(dtos, pageable, serviceEntities.getTotalElements());

        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> createService(K8sServiceDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());
        List<Service> services = serviceAdapterService.create(clusterEntity.getClusterId(), yaml);

//        List<Long> ids = services.stream().map(e -> {
//            String namespaceName = e.getMetadata().getNamespace();
//        })

        return null;
    }

    private ServiceEntity toServiceEntity(Service s) throws JsonProcessingException{
//        ObjectMapper mapper = new ObjectMapper();
//
//        //service_uid, service_name, created_at, type, cluster_ip, session_affinity,
//        // internal_endpoint, external_endpoint, selector, annotation, label
//        String uid = s.getMetadata().getUid();
//        String name = s.getMetadata().getName();
//        LocalDateTime createAt = DateUtil.strToLocalDateTime(s.getMetadata().getCreationTimestamp());
//        String type = s.getSpec().getType();
//        String clusterIp = s.getSpec().getClusterIP();
//        String sessionAffinity = s.getSpec().getSessionAffinity();
//        String selector = mapper.writeValueAsString(s.getSpec().getSelector());
//        String annotation = mapper.writeValueAsString(s.getMetadata().getAnnotations());
//        String label = mapper.writeValueAsString(s.getMetadata().getLabels());
//        String internalEndPoint = "";
//        String externalEndPoint = "";
//
//        if(ServiceType.NodePort.get().equals(type)){
//            externalEndPoint = mapper.writeValueAsString(s.getSpec().getPorts());
//        }else{
//            internalEndPoint = mapper.writeValueAsString(s.getSpec().getPorts());
//        }
//
//        ServiceEntity service = ServiceEntity.builder()
//                .serviceName(name)
//                .createdAt(createAt)
//                .serviceType(ServiceType.get(type))
//                .

        return null;
    }
}
