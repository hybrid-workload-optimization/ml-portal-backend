package kr.co.strato.portal.networking.v1.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointPort;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.adapter.k8s.endpoint.EndpointAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.service.model.ServiceEndpointEntity;
import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.model.ServiceType;
import kr.co.strato.domain.service.service.ServiceDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.networking.v1.model.K8sServiceDto;
import kr.co.strato.portal.networking.v1.model.K8sServiceDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class K8sServiceService extends InNamespaceService {
    @Autowired
    private ServiceDomainService serviceDomainService;

    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private ServiceAdapterService serviceAdapterService;

    @Autowired
    private EndpointAdapterService endpointAdapterService;
    
    @Autowired
   	ProjectDomainService projectDomainService;
    
    @Autowired
	ProjectAuthorityService projectAuthorityService;



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
        
        //이름 중복 채크
        duplicateCheckResourceCreation(clusterIdx, reqCreateDto.getYaml());
        
        ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());
        Long clusterId = clusterEntity.getClusterId();
        List<Service> services = serviceAdapterService.create(clusterId, yaml);

        List<Long> ids = services.stream().map(s -> {
            try {
                String namespaceName = s.getMetadata().getNamespace();
                ServiceEntity serviceEntity = toEntity(s);
                serviceEntity.setYaml(yaml);
                
                Endpoints endpoints = endpointAdapterService.get(clusterId, namespaceName, serviceEntity.getServiceName());
                List<ServiceEndpointEntity> serviceEndpoints = toEntities(endpoints);
                Long serviceId = serviceDomainService.register(serviceEntity, serviceEndpoints, clusterEntity, namespaceName);

                return serviceId;
            } catch (JsonParseException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the created service in the db");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateService(Long serviceId, K8sServiceDto.ReqUpdateDto reqUpdateDto){
        String yaml = Base64Util.decode(reqUpdateDto.getYaml());
        ClusterEntity clusterEntity = serviceDomainService.getClusterEntity(serviceId);
        Long clusterId = clusterEntity.getClusterId();

        List<Service> services = serviceAdapterService.update(clusterId, yaml);

        List<Long> ids = services.stream().map(s -> {
            try {
                String namespaceName = s.getMetadata().getNamespace();
                ServiceEntity serviceEntity = toEntity(s);
                serviceEntity.setYaml(yaml);
                
                Endpoints endpoints = endpointAdapterService.get(clusterId, namespaceName, serviceEntity.getServiceName());
                List<ServiceEndpointEntity> serviceEndpoints = toEntities(endpoints);

                return serviceDomainService.update(serviceId, serviceEntity, serviceEndpoints);
            } catch (JsonParseException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the updated service in the db");
            }
        }).collect(Collectors.toList());

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteService(Long id){
        ServiceEntity serviceEntity = serviceDomainService.get(id);
        Long clusterId = serviceEntity.getNamespace().getCluster().getClusterId();
        String namespaceName = serviceEntity.getNamespace().getName();
        String serviceName = serviceEntity.getServiceName();

        boolean isDeleted = serviceAdapterService.delete(clusterId, namespaceName, serviceName);
        if(isDeleted){
            return serviceDomainService.delete(id);
        }else{
            throw new InternalServerException("Fail to delete the k8s service");
        }
    }

    public String getServiceYaml(Long serviceId){
        ServiceEntity serviceEntity = serviceDomainService.get(serviceId);
        String yaml = serviceEntity.getYaml();
        
        if(yaml == null) {
        	 String serviceName = serviceEntity.getServiceName();
             String namespaceName = serviceEntity.getNamespace().getName();
             Long clusterId = serviceEntity.getNamespace().getCluster().getClusterId();

             yaml = serviceAdapterService.getYaml(clusterId, namespaceName, serviceName);
        }
        yaml = Base64Util.encode(yaml);
        return yaml;
    }

    public K8sServiceDto.ResDetailDto getService(Long serviceId, UserDto loginUser){
        ServiceEntity service = serviceDomainService.get(serviceId);
        Long clusterId = service.getNamespace().getCluster().getClusterId();
        String clusterName = service.getNamespace().getCluster().getClusterName();
        String namespaceName = service.getNamespace().getName();
        
        Long clusterIdx = service.getNamespace() .getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);

        Endpoints endpoints = endpointAdapterService.get(clusterId, namespaceName, service.getServiceName());

        List<ServiceEndpointEntity> endpointEntities = new ArrayList<>();
        try {
            endpointEntities = toEntities(endpoints);
        } catch (JsonProcessingException e) {
            endpointEntities = serviceDomainService.getServiceEndpoints(serviceId);
        }

        K8sServiceDto.ResDetailDto dto = K8sServiceDtoMapper.INSTANCE.toDetailDto(service, endpointEntities, clusterId, clusterName);
        dto.setProjectIdx(projectIdx);
        return dto;
    }


    private ServiceEntity toEntity(Service s) throws JsonParseException {
        Gson gson = new GsonBuilder().create();
        String uid = s.getMetadata().getUid();
        String name = s.getMetadata().getName();
        LocalDateTime createAt = DateUtil.strToLocalDateTime(s.getMetadata().getCreationTimestamp());
        String type = s.getSpec().getType();
        String clusterIp = s.getSpec().getClusterIP();
        String sessionAffinity = s.getSpec().getSessionAffinity();
        String selector = gson.toJson(s.getSpec().getSelector());
        String annotation = gson.toJson(s.getMetadata().getAnnotations());
        String label = gson.toJson(s.getMetadata().getLabels());
        String internalEndPoint = null;
        String externalEndPoint = null;
        internalEndPoint = gson.toJson(s.getSpec().getPorts());


        ServiceEntity service = ServiceEntity.builder()
                .serviceUid(uid)
                .serviceName(name)
                .createdAt(createAt)
                .type(ServiceType.get(type))
                .clusterIp(clusterIp)
                .sessionAffinity(sessionAffinity)
                .internalEndpoint(internalEndPoint)
                .externalEndpoint(externalEndPoint)
                .selector(selector)
                .annotation(annotation)
                .label(label)
                .build();

        return service;
    }

    public List<ServiceEndpointEntity> toEntities(Endpoints endpoints) throws JsonProcessingException{
        if(endpoints == null){
            return null;
        }
        List<ServiceEndpointEntity> entities = new ArrayList<>();
        List<EndpointSubset> subsets = endpoints.getSubsets();
        String name = endpoints.getMetadata().getName();
        String host = null;
        String nodeName = null;
        Integer port = null;
        String protocol = null;

        for(EndpointSubset subset : subsets){
            for(EndpointAddress address : subset.getAddresses()){
                host = address.getIp();
                nodeName = address.getNodeName();
                String ready = "true";
                for(EndpointAddress endpointAddress : subset.getNotReadyAddresses()){
                    if(host.equals(endpointAddress.getIp())){
                        ready = "false";
                        break;
                    }
                }
                for(EndpointPort endpointPort: subset.getPorts()){
                    port = endpointPort.getPort();
                    protocol = endpointPort.getProtocol();
                    ServiceEndpointEntity entity = ServiceEndpointEntity.builder()
                            .host(host)
                            .port(port)
                            .endpointName(name)
                            .protocol(protocol)
                            .ready(ready)
                            .nodeName(nodeName)
                            .build();

                    entities.add(entity);
                }
            }
        }

        return entities;
    }

	@Override
	protected InNamespaceDomainService getDomainService() {
		return serviceDomainService;
	}
	
	@Override
	public Long mlResourceApply(Long clusterIdx, Long resourceId, String yaml) {
		if(resourceId == null) {
			K8sServiceDto.ReqCreateDto reqCreateDto = new K8sServiceDto.ReqCreateDto();
			reqCreateDto.setClusterIdx(clusterIdx);
			reqCreateDto.setYaml(yaml);
			
			List<Long> list = createService(reqCreateDto);
			return list.get(0);
		} else {
			K8sServiceDto.ReqUpdateDto reqUpdateDto = new K8sServiceDto.ReqUpdateDto();
			reqUpdateDto.setYaml(yaml);
			
			List<Long> list = updateService(resourceId, reqUpdateDto);
			return list.get(0);
		}
	}
	
	@Override
	public boolean delete(Long resourceId, String yaml) {
		return deleteService(resourceId);
	}

}
