package kr.co.strato.portal.networking.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointPort;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.adapter.k8s.endpoint.EndpointAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.networking.model.K8sServiceOnlyApiDto;
import kr.co.strato.portal.networking.model.K8sServiceOnlyApiDto.ResEndPointListDto;
import kr.co.strato.portal.networking.model.K8sServiceOnlyApiDtoMapper;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class K8sServiceOnlyApiService {
	
    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private ServiceAdapterService serviceAdapterService;

    @Autowired
    private EndpointAdapterService endpointAdapterService;
    



    public Page<K8sServiceOnlyApiDto.ResListDto> getServices(Pageable pageable, K8sServiceOnlyApiDto.SearchParam searchParam) {
        ClusterEntity clusterEntity = clusterDomainService.get(searchParam.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
        
		List<Service> list = serviceAdapterService.getList(kubeConfigId, searchParam.getNamespace());
		List<K8sServiceOnlyApiDto.ResListDto> dtos = list.stream().map(e ->
        		(K8sServiceOnlyApiDto.ResListDto)K8sServiceOnlyApiDtoMapper.INSTANCE.toDto(e, false)).collect(Collectors.toList());
		dtos.stream().forEach(s -> s.setClusterName(clusterEntity.getClusterName()));
		Page<K8sServiceOnlyApiDto.ResListDto> result = new PageImpl<>(dtos, pageable, dtos.size());
        return result;
    }

    public void createService(K8sServiceOnlyApiDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());
        Long clusterId = clusterEntity.getClusterId();
        serviceAdapterService.create(clusterId, yaml);
    }

    public void updateService(K8sServiceOnlyApiDto.ReqCreateDto reqCreateDto) {
    	createService(reqCreateDto);
    }

    public boolean deleteService(Long clusterIdx, String namespace, String name) {
    	ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
        boolean isDeleted = serviceAdapterService.delete(kubeConfigId, namespace, name);
        return isDeleted;       
    }

    public String getServiceYaml(Long clusterIdx, String namespace, String name){
    	ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
		
		String yaml = serviceAdapterService.getYaml(kubeConfigId, namespace, name);
        yaml = Base64Util.encode(yaml);
        return yaml;
    }

    public K8sServiceOnlyApiDto.ResDetailDto getService(Long clusterIdx, String namespace, String name) {
    	ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
        
		Service svc = serviceAdapterService.get(kubeConfigId, namespace, name);
        
        Endpoints endpoints = endpointAdapterService.get(kubeConfigId, namespace, name);

        List<ResEndPointListDto> endpointList = new ArrayList<>();
        try {
        	endpointList = toEntities(endpoints);
        } catch (JsonProcessingException e) {
        	log.error("", e);
        }
        K8sServiceOnlyApiDto.ResDetailDto dto = (K8sServiceOnlyApiDto.ResDetailDto)K8sServiceOnlyApiDtoMapper.INSTANCE.toDto(svc, true);
        dto.setEndpoints(endpointList);
        dto.setClusterIdx(clusterIdx);
        dto.setClusterName(clusterEntity.getClusterName());
        return dto;
    }

    public List<ResEndPointListDto> toEntities(Endpoints endpoints) throws JsonProcessingException{
        if(endpoints == null){
            return null;
        }
        List<ResEndPointListDto> entities = new ArrayList<>();
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
                    ResEndPointListDto entity = ResEndPointListDto.builder()
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

}
