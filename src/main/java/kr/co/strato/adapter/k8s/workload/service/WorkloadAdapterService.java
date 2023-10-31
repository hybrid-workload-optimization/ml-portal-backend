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

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import kr.co.strato.adapter.k8s.common.model.ApplyResult;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.common.proxy.NonNamespaceProxy;
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
    private NonNamespaceProxy nonNamespaceProxy;
	
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
	public ApplyResult.Result apply(Long kubeConfigId, String yamlStr) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yamlStr).build();
        try{
        	List<HasMetadata> list = null;
        	ApplyResult.Response response = commonProxy.applyV2(param);
        	if(response.isSuccess()) {
        		list = new ArrayList<>();
        		String resourceStr = response.getResources();
        		
        		Yaml yaml = new Yaml();
        		ObjectMapper mapper = new ObjectMapper(); 
        		
        		
        		Iterable<Object> iter = yaml.loadAll(resourceStr);
        		
        		for(Object object : iter) {
        			if(object instanceof ArrayList) {
        				for(Object o : (ArrayList) object) {
        					if(o instanceof Map) {
        						try {						
        							Map map = (Map) o;
        							String kind = (String)map.get("kind");
        							
        							TypeReference t = getTypeReference(kind);
        							String jsonStr = mapper.writeValueAsString(map);
        							HasMetadata resource = (HasMetadata)mapper.readValue(jsonStr, t);
        							list.add(resource);
        						} catch (Exception e) {
        							log.error("", e);
        						}
        					}
        				}
        			}
        		}
        	}
        	
        	ApplyResult.Result result = ApplyResult.Result.builder()
        			.success(response.isSuccess())
        			.errorMessage(response.getErrorMessage())
        			.resources(list)
        			.build();
            
            return result;
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
		String yaml = null;
		if(namespace == null) {
			yaml = nonNamespaceProxy.getResourceYaml(resourceType, kubeConfigId, name);
		} else {
			yaml = inNamespaceProxy.getResourceYaml(resourceType, kubeConfigId, namespace, name);
		}
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
		} else if(type.equals("node")) {
			t = new TypeReference<Node>(){};
		} else if(type.equals("namespace")) {
			t = new TypeReference<Namespace>(){};
		} else if(type.equals("storageclass")) {
			t = new TypeReference<StorageClass>(){};
		} else if(type.equals("persistentvolume")) {
			t = new TypeReference<PersistentVolume>(){};
		} else if(type.equals("persistentvolumeclame")) {
			t = new TypeReference<PersistentVolumeClaim>(){};
		} else if(type.equals("secret")) {
			t = new TypeReference<Secret>(){};
		} else if(type.equals("configmap")) {
			t = new TypeReference<ConfigMap>(){};
		} else if(type.equals("service")) {
			t = new TypeReference<io.fabric8.kubernetes.api.model.Service>(){};
		} else if(type.equals("ingress")) {
			t = new TypeReference<Ingress>(){};
		}
		
		
		else {
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
			resType = ResourceType.daemonSet.get();
		}else if(lowerKind.equals("node")) {
			resType = ResourceType.node.get();
		} else if(lowerKind.equals("namespace")) {
			resType = ResourceType.namespace.get();
		} else if(lowerKind.equals("storageclass")) {
			resType = ResourceType.storageClass.get();
		} else if(lowerKind.equals("persistentvolume")) {
			resType = ResourceType.persistentVolume.get();
		} else if(lowerKind.equals("persistentvolumeclame")) {
			resType = ResourceType.pvc.get();
		} else if(lowerKind.equals("secret")) {
			resType = ResourceType.secret.get();
		} else if(lowerKind.equals("configmap")) {
			resType = ResourceType.configMap.get();
		} else if(lowerKind.equals("service")) {
			resType = ResourceType.service.get();
		} else if(lowerKind.equals("ingress")) {
			resType = ResourceType.ingress.get();
		}
		return resType;
	}
}
