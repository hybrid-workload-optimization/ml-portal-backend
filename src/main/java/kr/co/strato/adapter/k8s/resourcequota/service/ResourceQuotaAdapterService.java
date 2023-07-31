package kr.co.strato.adapter.k8s.resourcequota.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceQuotaAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<ResourceQuota> getList(Long kubeConfigId, String namespace) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(kubeConfigId)
				.namespace(namespace)
				.build();
		
		log.debug("[Get Resource Quota List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.resourceQuota.get(), body);
		log.debug("[Get Resource Quota List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ResourceQuota> result = mapper.readValue(response, new TypeReference<List<ResourceQuota>>(){});		
		return result;
	}
	
	public ReplicaSet get(Long kubeConfigId, String namespace, String name) throws Exception {
		log.debug("[Get Resource Quota] request : {}/{}/{}", kubeConfigId, namespace, name);
		String response = inNamespaceProxy.getResource(ResourceType.resourceQuota.get(), kubeConfigId, namespace, name);
		log.debug("[Get Resource Quota] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		ReplicaSet result = mapper.readValue(response, new TypeReference<ReplicaSet>(){});
		
		return result;
	}
	
	public List<ReplicaSet> create(Long kubeConfigId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(kubeConfigId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Resource Quota] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Resource Quota] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ReplicaSet> result = mapper.readValue(response, new TypeReference<List<ReplicaSet>>(){});
		
		return result;
	}
	
	public boolean delete(Long kubeConfigId, String namespace, String name) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(kubeConfigId)
                .namespace(namespace)
                .name(name)
                .build();
		
		log.debug("[Delete Resource Quota] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.resourceQuota.get(), body);
		log.debug("[Delete Resource Quota] response : {}", response);
		return response;
	}
	
	public String getYaml(Long kubeConfigId, String namespace, String name) throws Exception {
		log.debug("[Get Resource Quota Yaml] request : {}/{}/{}", kubeConfigId, namespace, name);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.resourceQuota.get(), kubeConfigId, namespace, name);
		log.debug("[Get Resource Quota Yaml] response : {}", response);
		return response;
	}
}
