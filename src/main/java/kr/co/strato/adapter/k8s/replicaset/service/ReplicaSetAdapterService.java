package kr.co.strato.adapter.k8s.replicaset.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class ReplicaSetAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<ReplicaSet> getList(Long clusterId) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.build();
		
		log.debug("[Get Replica Set List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.replicaSet.get(), body);
		log.debug("[Get Replica Set List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ReplicaSet> result = mapper.readValue(response, new TypeReference<List<ReplicaSet>>(){});
		
		return result;
	}
	
	public ReplicaSet get(Long clusterId, String namespaceName, String replicaSetName) throws Exception {
		log.debug("[Get Replica Set] request : {}/{}/{}", clusterId, namespaceName, replicaSetName);
		String response = inNamespaceProxy.getResource(ResourceType.replicaSet.get(), clusterId, namespaceName, replicaSetName);
		log.debug("[Get Replica Set] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		ReplicaSet result = mapper.readValue(response, new TypeReference<ReplicaSet>(){});
		
		return result;
	}
	
	public List<ReplicaSet> create(Long clusterId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(clusterId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Replica Set] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Replica Set] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<ReplicaSet> result = mapper.readValue(response, new TypeReference<List<ReplicaSet>>(){});
		
		return result;
	}
	
	public boolean delete(Long clusterId, String namespaceName, String replicaSetName) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(replicaSetName)
                .build();
		
		log.debug("[Delete Replica Set] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.replicaSet.get(), body);
		log.debug("[Delete Replica Set] response : {}", response);
		
		return response;
	}
	
	public String getYaml(Long clusterId, String namespaceName, String replicaSetName) throws Exception {
		log.debug("[Get Replica Set Yaml] request : {}/{}/{}", clusterId, namespaceName, replicaSetName);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.replicaSet.get(), clusterId, namespaceName, replicaSetName);
		log.debug("[Get Replica Set Yaml] response : {}", response);
		
		return response;
	}

	public List<ReplicaSet> getListFromOwnerUid(Long clusterId, String ownerUid) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.ownerUid(ownerUid)
				.build();

		log.debug("[Get Replica Set List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.replicaSet.get(), body);
		log.debug("[Get Replica Set List] response : {}", response);

		ObjectMapper mapper = new ObjectMapper();
		List<ReplicaSet> result = mapper.readValue(response, new TypeReference<List<ReplicaSet>>(){});

		return result;
	}
}
