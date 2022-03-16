package kr.co.strato.adapter.k8s.pod.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.common.proxy.PodProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.portal.workload.model.PodDto;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PodAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;
    
    @Autowired
    private PodProxy podProxy;

    /**
     * k8s Pod 생성
     * @param clusterId
     * @param yaml
     * @return
     */
    public List<Pod> create(Long clusterId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);

            ObjectMapper mapper = new ObjectMapper();
            List<Pod> pods = mapper.readValue(results, new TypeReference<List<Pod>>(){});

            return pods;

        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Pod 생성 에러");
        }
    }

    public List<Pod> update(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
//            System.out.println(results);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<Pod> pods = mapper.readValue(results, new TypeReference<List<Pod>>(){});

            return pods;

        }catch (FeignException e){
//            log.error(e.getMessage(), e);
            e.printStackTrace();
            System.out.println(e.request());
            throw new InternalServerException("k8s interface 통신 에러 - Pod 업데이트 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Pod 업데이트 에러");
        }
    }

    /**
     * Pod 삭제
     * @param clusterId
     * @param namespaceName
     * @param podName
     * @return
     */
    public boolean delete(Long clusterId, String namespaceName, String podName){
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(podName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.pod.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - pod 삭제 에러");
        }
        
    }

    /**
     * Pod 조회
     * @param clusterId
     * @param namespaceName
     * @param podName
     * @return
     */
    public Pod get(Long clusterId, String namespaceName, String podName){
        try{
            String res = inNamespaceProxy.getResource(ResourceType.pod.get(), clusterId, namespaceName, podName);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            Pod pod = mapper.readValue(res, new TypeReference<Pod>(){});

            return pod;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - pod 조회 에러");
        }
    }

    public String getYaml(Long clusterId, String namespaceName, String podName){
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.pod.get(), clusterId, namespaceName, podName);

            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - pod yaml 조회 에러");
        }
    }
    
    public List<Pod> getList(PodDto.OwnerSearchParam searchParam) {
    	try {
    		Long clusterId = searchParam.getClusterId();
    		String nodeName = searchParam.getNodeName();
    		String ownerUid = searchParam.getNodeName();
    		String namespace = searchParam.getNamespace();
    		Map<String, String> selector = searchParam.getSelector();
    		String storageClass = searchParam.getStorageClass();
    		ResourceListSearchInfo param = ResourceListSearchInfo.builder()
    				.kubeConfigId(clusterId)
    				.nodeName(nodeName)
    				.ownerUid(ownerUid)
    				.namespace(namespace)
    				.selector(selector)
    				.storageClass(storageClass)
    				.build();
    		String results = inNamespaceProxy.getResourceList(ResourceType.pod.get(), param);
    		
    		ObjectMapper mapper = new ObjectMapper();
    		List<Pod> pods = mapper.readValue(results, new TypeReference<List<Pod>>() {});
    		
    		return pods;
    	}catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - pod 조회 에러");
        }
    }
    
    public ResponseEntity<ByteArrayResource> getLogDownloadFile(Long clusterId, String namespaceName, String podName) {
    	try {
    		ResponseEntity<ByteArrayResource> entity = podProxy.getResourceLogFile(clusterId, namespaceName, podName);
    		return entity;
    	}catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - pod log download 에러");
        }
    }
}
