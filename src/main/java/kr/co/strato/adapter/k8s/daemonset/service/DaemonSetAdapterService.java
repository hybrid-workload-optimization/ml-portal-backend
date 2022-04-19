package kr.co.strato.adapter.k8s.daemonset.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
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
public class DaemonSetAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<DaemonSet> getList(Long clusterId) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.build();
		
		log.debug("[Get Daemon Set List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.daemonSet.get(), body);
		log.debug("[Get Daemon Set List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<DaemonSet> result = mapper.readValue(response, new TypeReference<List<DaemonSet>>(){});
		
		return result;
	}
	
	public List<DaemonSet> create(Long clusterId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(clusterId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Daemon Set] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Daemon Set] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<DaemonSet> result = mapper.readValue(response, new TypeReference<List<DaemonSet>>(){});
		
		return result;
	}
	
	public DaemonSet get(Long clusterId, String namespaceName, String daemonSetName) throws Exception {
		log.debug("[Get Daemon Set] request : {}/{}/{}", clusterId, namespaceName, daemonSetName);
		String response = inNamespaceProxy.getResource(ResourceType.daemonSet.get(), clusterId, namespaceName, daemonSetName);
		log.debug("[Get Daemon Set] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		DaemonSet result = mapper.readValue(response, new TypeReference<DaemonSet>(){});
		
		return result;
	}
	
	public String getYaml(Long clusterId, String namespaceName, String daemonSetName) throws Exception {
		log.debug("[Get Daemon Set Yaml] request : {}/{}/{}", clusterId, namespaceName, daemonSetName);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.daemonSet.get(), clusterId, namespaceName, daemonSetName);
		log.debug("[Get Daemon Set Yaml] response : {}", response);
		
		return response;
	}
	
	public boolean delete(Long clusterId, String namespaceName, String daemonSetName) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(daemonSetName)
                .build();
		
		log.debug("[Delete Daemon Set] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.daemonSet.get(), body);
		log.debug("[Delete Daemon Set] response : {}", response);
		
		return response;
	}
}
