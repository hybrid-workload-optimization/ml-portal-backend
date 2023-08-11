package kr.co.strato.adapter.k8s.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CommonAdapterService {
	
	@Autowired
    private CommonProxy commonProxy;
	
	public List<HasMetadata> create(Long kubeConfigId, String yamlStr) {
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
	
	public boolean delete(Long clusterId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            boolean results = commonProxy.delete(param);
            return results;
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - 리소스 생성 에러");
        }
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
	
}
