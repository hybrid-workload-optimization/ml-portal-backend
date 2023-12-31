package kr.co.strato.adapter.k8s.node.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.Node;
import kr.co.strato.adapter.k8s.common.model.ClusterResourceInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.NonNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NodeAdapterService {
	@Autowired
	private NonNamespaceProxy nonNamespaceProxy;
    @Autowired
    private CommonProxy commonProxy;

    /**
     * node 리스트 조회(공통 feign 인터페이스 사용 시)
     * @param kubeConfigId
     * @return
     * @throws JsonProcessingException
     */
    public List<Node> getNodeList(Long kubeConfigId) {
		// 요청 파라미터 객체 생성
		ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(kubeConfigId).build();

		// 조회 요청
		String results = nonNamespaceProxy.getResourceList(ResourceType.node.get(), param);
		//log.debug("[Get Node List] response : {}", results);
		
		try {
			// json -> fabric8 k8s 오브젝트 파싱
			// DataMapping 모듈 ObjectMapper -> gson 으로 변경 2022-03-31 이호철
			
			Gson gson = new GsonBuilder().create();
			List<Node> clusterNodes = gson.fromJson(results, TypeToken.getParameterized(List.class, Node.class).getType());
			
			return clusterNodes;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		}
	}
    
    
    
    public Node getNodeDetail(Long kubeConfigId,String name) {
		// 요청 파라미터 객체 생성
		ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(kubeConfigId).name(name).build();

		// 조회 요청
		String result = nonNamespaceProxy.getResource(ResourceType.node.get(), param.getKubeConfigId(),param.getName());
		log.debug("[Get Node List] response : {}", result);
		try {
			// json -> fabric8 k8s 오브젝트 파싱
			ObjectMapper mapper = new ObjectMapper();
			Gson gson = new GsonBuilder().create();

			Node clusterNode = gson.fromJson(result, Node.class);
			//Node clusterNode = mapper.readValue(result, Node.class);

			return clusterNode;
		}catch(FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - 에러");
        }catch(JsonParseException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 -  에러");
        }
	}
    
    
    public List<Node> registerNode(Long kubeConfigId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yaml).build();

        try{
            String results = commonProxy.apply(param);
            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<Node> clusterNodes = mapper.readValue(results, new TypeReference<List<Node>>(){});

            return clusterNodes;

        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteNode(Integer kubeConfigId, String name){
    	ClusterResourceInfo param = ClusterResourceInfo.builder().kubeConfigId(kubeConfigId).name(name).build();
        return nonNamespaceProxy.deleteResource(ResourceType.node.get(), param);
    }
    
    public String getNodeYaml(Long kubeConfigId,String name) {

  		String nodeYaml = nonNamespaceProxy.getResourceYaml(ResourceType.node.get(), kubeConfigId,name);
  		return nodeYaml;
  	}
    
    /**
	 * worker node ip 반환.
	 * @param kubeConfigId
	 * @return
	 */
	public List<String> getWorkerNodeIps(Long kubeConfigId) {
		List<Node> list = getWorkerNodes(kubeConfigId);
		List<String> ips = new ArrayList<>();
		for(Node node: list) {
			String ip = node.getStatus().getAddresses().stream()
					.filter(addr -> addr.getType().equals("InternalIP"))
					.map(addr -> addr.getAddress())
					.findFirst()
					.get();
			
			if(ip != null) {
				ips.add(ip);
			}
		}
		return ips;
	}
	
	/**
	 * Worker 노드 반환.
	 * @param kubeConfigId
	 * @return
	 */
	public List<Node> getWorkerNodes(Long kubeConfigId) {
		List<Node> list = getNodeList(kubeConfigId);
		List<Node> newList = new ArrayList<>();
		for(Node node: list) {
			List<String> roles = new ArrayList<>();
			node.getMetadata().getLabels().keySet().stream()
					.filter(l -> l.contains("node-role"))
					.map(l -> l.split("/")[1])
					.iterator().forEachRemaining(roles::add);
			
			if(roles.size() == 0 || roles.contains("worker")) {
				newList.add(node);
			}
		}
		return newList;
	}
    
    
}
