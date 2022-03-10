package kr.co.strato.portal.networking.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.model.ServiceType;
import kr.co.strato.domain.service.service.ServiceDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.networking.model.K8sServiceDto;
import kr.co.strato.portal.networking.model.K8sServiceDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
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

        List<Long> ids = services.stream().map(e -> {
            try {
                String namespaceName = e.getMetadata().getNamespace();
                ServiceEntity serviceEntity = toEntity(e);

                return serviceDomainService.register(serviceEntity, clusterEntity, namespaceName);
            } catch (JsonProcessingException ex) {
                log.error(ex.getMessage(), ex);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new InternalServerException("statefulSet register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    private ServiceEntity toEntity(Service s) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();

        String uid = s.getMetadata().getUid();
        String name = s.getMetadata().getName();
        LocalDateTime createAt = DateUtil.strToLocalDateTime(s.getMetadata().getCreationTimestamp());
        String type = s.getSpec().getType();
        String clusterIp = s.getSpec().getClusterIP();
        String sessionAffinity = s.getSpec().getSessionAffinity();
        String selector = mapper.writeValueAsString(s.getSpec().getSelector());
        String annotation = mapper.writeValueAsString(s.getMetadata().getAnnotations());
        String label = mapper.writeValueAsString(s.getMetadata().getLabels());
        String internalEndPoint = null;
        String externalEndPoint = null;

        if(ServiceType.NodePort.get().equals(type)){
            externalEndPoint = mapper.writeValueAsString(s.getSpec().getPorts());
        }else{
            internalEndPoint = mapper.writeValueAsString(s.getSpec().getPorts());
        }

        ServiceEntity service = ServiceEntity.builder()
                .serviceUid(uid)
                .serviceName(name)
                .createdAt(createAt)
                .serviceType(ServiceType.get(type))
                .clusterIp(clusterIp)
                .sessionAffinity(sessionAffinity)
                .internalEndpoint(internalEndPoint)
                .externalEndpoint(externalEndPoint)
                .seletor(selector)
                .annotation(annotation)
                .label(label)
                .build();

        return service;
    }
}
