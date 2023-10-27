package kr.co.strato.portal.networking.v2.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointPort;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.adapter.k8s.endpoint.EndpointAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.networking.v2.model.NetworkCommonDto;
import kr.co.strato.portal.networking.v2.model.ServiceDto;
import kr.co.strato.portal.networking.v2.model.ServiceDto.EndpointListDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import kr.co.strato.portal.workload.v2.service.WorkloadCommonV2;
import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class K8SServiceServiceV2 extends WorkloadCommonV2 {
	
    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private ServiceAdapterService serviceAdapterService;

    @Autowired
    private EndpointAdapterService endpointAdapterService;
    


    /**
     * Service List 조회.
     * @param clusterIdx
     * @return
     */
    public List<ServiceDto> getList(Long clusterIdx) throws Exception {
        ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
        
		List<ServiceDto> result = new ArrayList<>();
		List<Service> list = serviceAdapterService.getList(kubeConfigId, null);
		for(Service s: list) {
			ServiceDto dto = (ServiceDto)toDto(clusterEntity, s);
			result.add(dto);
		}
        return result;
    }

    /**
     * 삭제
     * @param search
     * @return
     */
    public boolean delete(NetworkCommonDto.Search search) {
    	ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
        boolean isDeleted = serviceAdapterService.delete(kubeConfigId, search.getNamespace(), search.getName());
        return isDeleted;       
    }

    /**
     * Yaml 조회
     * @param search
     * @return
     */
    public String getYaml(NetworkCommonDto.Search search) {
    	ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		
		String yaml = serviceAdapterService.getYaml(kubeConfigId, search.getNamespace(), search.getName());
        yaml = Base64Util.encode(yaml);
        return yaml;
    }

    /**
     * 상세 조회
     * @param search
     * @return
     */
    public ServiceDto getDetail(NetworkCommonDto.Search search) throws Exception {
    	ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
        
		Service svc = serviceAdapterService.get(kubeConfigId, search.getNamespace(), search.getName());        
   
        List<EndpointListDto> endpointList = null;
        try {
        	 Endpoints endpoints = endpointAdapterService.get(kubeConfigId, search.getNamespace(), search.getName());
        	endpointList = getEndPoints(endpoints);
        } catch (JsonProcessingException e) {
        	log.error("", e);
        }
        
        ServiceDto dto = (ServiceDto)toDto(clusterEntity, svc);
        dto.setEndpoints(endpointList);
        return dto;
    }

    public List<EndpointListDto> getEndPoints(Endpoints endpoints) throws JsonProcessingException{
        if(endpoints == null){
            return null;
        }
        List<EndpointListDto> entities = new ArrayList<>();
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
                    
                    EndpointListDto entity = EndpointListDto.builder()
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
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		ServiceDto dto = new ServiceDto();
		setMetadataInfo(data, dto);
		
		Service s = (Service)data;
		
		Gson gson = new GsonBuilder().create();
		
        String type = s.getSpec().getType();
        String clusterIp = s.getSpec().getClusterIP();
        String sessionAffinity = s.getSpec().getSessionAffinity();
        Map<String, String> selector = s.getSpec().getSelector();
        
        String internalEndPoint = gson.toJson(s.getSpec().getPorts());
        String externalEndPoint = null;
        //String externalEndPoint = gson.toJson(s.getSpec().getExternalIPs());
        
		dto.setType(type);
		dto.setClusterIp(clusterIp);
		dto.setSessionAffinity(sessionAffinity);
		dto.setSelector(selector);
		dto.setInternalEndpoints(stringToMapList(internalEndPoint));
		dto.setExternalEndpoints(stringToMapList(externalEndPoint));
		
		return dto;
	}
	
	private List<HashMap<String, Object>> stringToMapList(String text){
        if(text == null){
            return null;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> maps = mapper.readValue(text, new TypeReference<List<HashMap<String, Object>>>() {
            });
            return maps;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

}
