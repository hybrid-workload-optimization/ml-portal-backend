package kr.co.strato.adapter.k8s.persistentVolumeClaim.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.daemonset.service.DaemonSetAdapterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersistentVolumeClaimAdapterService {

	@Autowired
    private CommonProxy commonProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	public List<PersistentVolumeClaim> create(Long clusterId, String yaml) throws Exception {
		YamlApplyParam body = YamlApplyParam.builder()
				.kubeConfigId(clusterId)
				.yaml(yaml)
				.build();
		
		log.debug("[Create Persistent Volume Claim] request : {}", body.toString());
		String response = commonProxy.apply(body);
		log.debug("[Create Persistent Volume Claim] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<PersistentVolumeClaim> result = mapper.readValue(response, new TypeReference<List<PersistentVolumeClaim>>(){});
		
		return result;
	}
	
	public List<PersistentVolumeClaim> getList(Long clusterId) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.build();
		
		log.debug("[Get Persistent Volume Claim List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.pvc.get(), body);
		log.debug("[Get Persistent Volume Claim List] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<PersistentVolumeClaim> result = mapper.readValue(response, new TypeReference<List<PersistentVolumeClaim>>(){});
		
		return result;
	}
	
	public PersistentVolumeClaim get(Long clusterId, String namespaceName, String persistentVolumeClaimName) throws Exception {
		log.debug("[Get Persistent Volume Claim] request : {}/{}/{}", clusterId, namespaceName, persistentVolumeClaimName);
		String response = inNamespaceProxy.getResource(ResourceType.pvc.get(), clusterId, namespaceName, persistentVolumeClaimName);
		log.debug("[Get Persistent Volume Claim] response : {}", response);
		
		ObjectMapper mapper = new ObjectMapper(); 
		PersistentVolumeClaim result = mapper.readValue(response, new TypeReference<PersistentVolumeClaim>(){});
		
		return result;
	}
	
	public String getYaml(Long clusterId, String namespaceName, String persistentVolumeClaimName) throws Exception {
		log.debug("[Get Persistent Volume Claim Yaml] request : {}/{}/{}", clusterId, namespaceName, persistentVolumeClaimName);
		String response = inNamespaceProxy.getResourceYaml(ResourceType.pvc.get(), clusterId, namespaceName, persistentVolumeClaimName);
		log.debug("[Get Persistent Volume Claim Yaml] response : {}", response);
		
		return response;
	}
	
	public boolean delete(Long clusterId, String namespaceName, String persistentVolumeClaimName) throws Exception {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(persistentVolumeClaimName)
                .build();
		
		log.debug("[Delete Persistent Volume Claim] request : {}", body.toString());
		boolean response = inNamespaceProxy.deleteResource(ResourceType.pvc.get(), body);
		log.debug("[Delete Persistent Volume Claim] response : {}", response);
		
		return response;
	}
}
