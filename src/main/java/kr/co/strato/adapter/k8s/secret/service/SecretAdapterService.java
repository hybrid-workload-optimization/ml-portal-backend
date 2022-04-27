package kr.co.strato.adapter.k8s.secret.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.configMap.service.ConfigMapAdapterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SecretAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<Secret> create(Long clusterId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(clusterId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Secret] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Secret] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<Secret> result = mapper.readValue(response, new TypeReference<List<Secret>>(){});
		
		return result;
	}
	
	public List<Secret> getList(Long clusterId) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.build();
		
		log.debug("[Get Secret List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.secret.get(), body);
		log.debug("[Get Secret List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<Secret> result = mapper.readValue(response, new TypeReference<List<Secret>>(){});
		
		return result;
	}
	
	public Secret get(Long clusterId, String namespaceName, String secretName) throws Exception {
		log.debug("[Get Secret] request : {}/{}/{}", clusterId, namespaceName, secretName);
		String response = inNamespaceProxy.getResource(ResourceType.secret.get(), clusterId, namespaceName, secretName);
		log.debug("[Get Secret] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		Secret result = mapper.readValue(response, new TypeReference<Secret>(){});
		
		return result;
	}
	
	public String getYaml(Long clusterId, String namespaceName, String secretName) throws Exception {
		log.debug("[Get Secret Yaml] request : {}/{}/{}", clusterId, namespaceName, secretName);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.secret.get(), clusterId, namespaceName, secretName);
		log.debug("[Get Secret Yaml] response : {}", response);
		
		return response;
	}
	
	public boolean delete(Long clusterId, String namespaceName, String secretName) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(secretName)
                .build();
		
		log.debug("[Delete Secret] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.secret.get(), body);
		log.debug("[Delete Secret] response : {}", response);
		
		return response;
	}
}
