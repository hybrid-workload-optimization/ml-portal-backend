package kr.co.strato.adapter.k8s.configMap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.persistentVolumeClaim.service.PersistentVolumeClaimAdapterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfigMapAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<ConfigMap> create(Long clusterId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(clusterId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Config Map] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Config Map] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ConfigMap> result = mapper.readValue(response, new TypeReference<List<ConfigMap>>(){});
		
		return result;
	}
	
	public List<ConfigMap> getList(Long clusterId) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.build();
		
		log.debug("[Get Config Map List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.configMap.get(), body);
		log.debug("[Get Config Map List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ConfigMap> result = mapper.readValue(response, new TypeReference<List<ConfigMap>>(){});
		
		return result;
	}
	
	public ConfigMap get(Long clusterId, String namespaceName, String configMapName) throws Exception {
		log.debug("[Get Config Map] request : {}/{}/{}", clusterId, namespaceName, configMapName);
		String response = inNamespaceProxy.getResource(ResourceType.configMap.get(), clusterId, namespaceName, configMapName);
		log.debug("[Get Config Map] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		ConfigMap result = mapper.readValue(response, new TypeReference<ConfigMap>(){});
		
		return result;
	}
	
	public String getYaml(Long clusterId, String namespaceName, String configMapName) throws Exception {
		log.debug("[Get Config Map Yaml] request : {}/{}/{}", clusterId, namespaceName, configMapName);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.configMap.get(), clusterId, namespaceName, configMapName);
		log.debug("[Get Config Map Yaml] response : {}", response);
		
		return response;
	}
	
	public boolean delete(Long clusterId, String namespaceName, String configMapName) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(configMapName)
                .build();
		
		log.debug("[Delete Config Map] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.configMap.get(), body);
		log.debug("[Delete Config Map] response : {}", response);
		
		return response;
	}
}
