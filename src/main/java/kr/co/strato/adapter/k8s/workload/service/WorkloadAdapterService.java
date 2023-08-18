package kr.co.strato.adapter.k8s.workload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.common.proxy.WorkloadProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkloadAdapterService {

	@Autowired
	private WorkloadProxy workloadProxy;
	
	@Autowired
    private InNamespaceProxy inNamespaceProxy;
	
	@Autowired
	private CommonProxy commonProxy;
	
	/**
	 * 워크로드 리스트 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<HasMetadata> getList(ResourceListSearchInfo param) throws Exception {		
		log.debug("[Get Workload List] request : {}", param.toString());
		String response = workloadProxy.getWorkloadList(param);
		log.debug("[Get Config Map List] response : {}", response);
		
		Yaml yaml = new Yaml();
		ObjectMapper mapper = new ObjectMapper(); 
		
		List<HasMetadata> list = new ArrayList<>();
		Iterable<Object> iter = yaml.loadAll(response);
		
		for(Object object : iter) {
			if(object instanceof ArrayList) {
				for(Object o : (ArrayList) object) {
					if(o instanceof Map) {
						try {						
							Map map = (Map) o;
							String kind = (String)map.get("kind");
							
							TypeReference t = getTypeReference(kind);
							String jsonStr = mapper.writeValueAsString(map);
							HasMetadata result = (HasMetadata)mapper.readValue(jsonStr, t);
							list.add(result);
						} catch (Exception e) {
							log.error("", e);
						}
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 워크로드 상세 조회
	 * @param kind
	 * @param clusterId
	 * @param namespaceName
	 * @param podName
	 * @return
	 * @throws Exception
	 */
	public HasMetadata getDetail(Long kubeconfigId, String kind, String namespaceName, String podName) {	
		try {
			String resType = getResourceType(kind);
			if(resType == null) {
				log.error("지원하지 않는 리소스 타입 입니다. kind:  {}", kind);
			}
			
            String res = inNamespaceProxy.getResource(resType, kubeconfigId, namespaceName, podName);
            
            
            ObjectMapper mapper = new ObjectMapper(); 
            TypeReference t = getTypeReference(kind);
			HasMetadata result = (HasMetadata)mapper.readValue(res, t);

            return result;
        } catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - pod 조회 에러");
        }
	}
	
	/**
	 * 워크로드 리소스 생성 및 업데이트
	 * @param kubeConfigId
	 * @param yamlStr
	 * @return
	 */
	public List<HasMetadata> apply(Long kubeConfigId, String yamlStr) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yamlStr).build();
        try{
            String response = commonProxy.apply(param);            
            
            Yaml yaml = new Yaml();
    		ObjectMapper mapper = new ObjectMapper(); 
    		
    		List<HasMetadata> list = new ArrayList<>();
    		Iterable<Object> iter = yaml.loadAll(response);
    		
    		for(Object object : iter) {
    			if(object instanceof ArrayList) {
    				for(Object o : (ArrayList) object) {
    					if(o instanceof Map) {
    						try {						
    							Map map = (Map) o;
    							String kind = (String)map.get("kind");
    							
    							TypeReference t = getTypeReference(kind);
    							String jsonStr = mapper.writeValueAsString(map);
    							HasMetadata result = (HasMetadata)mapper.readValue(jsonStr, t);
    							list.add(result);
    						} catch (Exception e) {
    							log.error("", e);
    						}
    					}
    				}
    			}
    		}
            return list;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - 리소스 생성 에러");
        }
    }
	
	/**
	 * 리소스 삭제
	 * @param kubeConfigId
	 * @param kind
	 * @param namespace
	 * @param name
	 * @return
	 */
	public boolean delete(Long kubeConfigId, String kind, String namespace, String name) {
		WorkloadResourceInfo body = WorkloadResourceInfo.builder()
                .kubeConfigId(kubeConfigId)
                .namespace(namespace)
                .name(name)
                .build();
		
		String resourceType = getResourceType(kind);
		boolean response = inNamespaceProxy.deleteResource(resourceType, body);
		return response;
	}
	
	/**
	 * 리소스 yaml 정보 조회
	 * @param kubeConfigId
	 * @param kind
	 * @param namespace
	 * @param name
	 * @return
	 */
	public String resourceYaml(Long kubeConfigId, String kind, String namespace, String name) {
		String resourceType = getResourceType(kind);
		String yaml = inNamespaceProxy.getResourceYaml(resourceType, kubeConfigId, namespace, name);
		return yaml;
	}
	
	
	
	private TypeReference getTypeReference(String kind) {
		String type = kind.toLowerCase();
		
		TypeReference t = null;
		if(type.equals("deployment")) {
			t = new TypeReference<Deployment>(){};
		} else if(type.equals("statefulset")) {
			t = new TypeReference<StatefulSet>(){};
		} else if(type.equals("pod")) {
			t = new TypeReference<Pod>(){};
		} else if(type.equals("job")) {
			t = new TypeReference<Job>(){};
		} else if(type.equals("replicaset")) {
			t = new TypeReference<ReplicaSet>(){};
		} else if(type.equals("daemonset")) {
			t = new TypeReference<DaemonSet>(){};
		} else {
			t = new TypeReference<HasMetadata>(){};
		}		
		return t;
	}
	
	private String getResourceType(String kind) {
		String lowerKind = kind.toLowerCase();
		String resType = null;
		if(lowerKind.equals("deployment")) {
			resType = ResourceType.deployment.get();
		} else if(lowerKind.equals("statefulset")) {
			resType = ResourceType.statefulSet.get();
		} else if(lowerKind.equals("pod")) {
			resType = ResourceType.pod.get();
		} else if(lowerKind.equals("cronjob")) {
			resType = ResourceType.cronJob.get();
		} else if(lowerKind.equals("job")) {
			resType = ResourceType.job.get();
		} else if(lowerKind.equals("replicaset")) {
			resType = ResourceType.replicaSet.get();
		} else if(lowerKind.equals("daemonset")) {
			resType = ResourceType.daemonSet.get();;
		}
		return resType;
	}
}
