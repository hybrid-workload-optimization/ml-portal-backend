package kr.co.strato.adapter.k8s.workload.service;

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
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.proxy.WorkloadProxy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkloadAdapterService {

	@Autowired
	private WorkloadProxy workloadProxy;
	
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
