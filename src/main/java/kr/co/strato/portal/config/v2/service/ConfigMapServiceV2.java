package kr.co.strato.portal.config.v2.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import kr.co.strato.adapter.k8s.configMap.service.ConfigMapAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.portal.config.v2.model.ConfigCommonDto;
import kr.co.strato.portal.config.v2.model.ConfigMapDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import kr.co.strato.portal.workload.v2.service.WorkloadCommonV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfigMapServiceV2 extends WorkloadCommonV2 {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ConfigMapAdapterService configMapAdapterService;
	
	
	
	/**
	 * Config Map 목록 조회
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public List<ConfigMapDto> getList(Long clusterIdx) throws Exception {
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = entity.getClusterId();
		
		List<ConfigMap> list = configMapAdapterService.getList(kubeConfigId);
		List<ConfigMapDto> result = new ArrayList<>();
		if(list != null) {
			for(ConfigMap c : list) {
				ConfigMapDto dto = (ConfigMapDto)toDto(entity, c);
				result.add(dto);
			}
		}
		return result;
	}
	
	/**
	 * Config Map 상세 조회
	 * 
	 * @param configMapIdx
	 * @return
	 * @throws Exception
	 */
	public ConfigMapDto getDetail(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
		
		ConfigMap c = configMapAdapterService.get(kubeConfigId, search.getNamespace(), search.getName());
		ConfigMapDto dto = null;
		if(c != null) {
			dto = (ConfigMapDto)toDto(entity, c);
		}
		return dto;
	}
	
	/**
	 * Config Map Yaml 조회
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public String getYaml(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
		
		String yaml = configMapAdapterService.getYaml(kubeConfigId, search.getNamespace(), search.getName());
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}

	
	/**
	 * Config Map 삭제
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public boolean delete(ConfigCommonDto.Search search) throws Exception {
		ClusterEntity entity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = entity.getClusterId();
		
        
		boolean isDeleted = configMapAdapterService.delete(kubeConfigId, search.getNamespace(), search.getName());
		if (!isDeleted) {
			throw new PortalException("Config Map deletion failed");
		}
		return isDeleted;
	}

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		return toDto(data);
	}
	
	public ConfigMapDto toDto(HasMetadata data) {
		ConfigMapDto dto = new ConfigMapDto();
		setMetadataInfo(data, dto);
		
		ObjectMapper mapper = new ObjectMapper();
    	String d = null;
		try {
			d = mapper.writeValueAsString(((ConfigMap)data).getData());
		} catch (JsonProcessingException e) {
			log.error("", e);
		}
		dto.setData(d);
		return dto;
	}
}
