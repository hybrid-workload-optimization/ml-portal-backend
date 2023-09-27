package kr.co.strato.adapter.k8s.storageClass.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
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
public class StorageClassAdapterService {
	@Autowired
	private NonNamespaceProxy nonNamespaceProxy;
    @Autowired
    private CommonProxy commonProxy;

    /**
     * @param kubeConfigId
     * @return
     * @throws JsonProcessingException
     */
    public List<StorageClass> getStorageClassList(Long kubeConfigId) {
		// 요청 파라미터 객체 생성
		ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(kubeConfigId).build();

		// 조회 요청
		String results = nonNamespaceProxy.getResourceList(ResourceType.storageClass.get(), param);

		try {
			// json -> fabric8 k8s 오브젝트 파싱
			ObjectMapper mapper = new ObjectMapper();
			List<StorageClass> clusterStorageClasss = mapper.readValue(results, new TypeReference<List<StorageClass>>() {
			});

			return clusterStorageClasss;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		}
	}
    
    public StorageClass getStorageClass(Long kubeConfigId, String name) {
    	// 조회 요청
		String results = nonNamespaceProxy.getResource(ResourceType.storageClass.get(), kubeConfigId, name);

		try {
			// json -> fabric8 k8s 오브젝트 파싱
			ObjectMapper mapper = new ObjectMapper();
			StorageClass storageClass = mapper.readValue(results, new TypeReference<StorageClass>() {});

			return storageClass;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		}
	}
    
    
    public List<StorageClass> registerStorageClass(Long kubeConfigId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yaml).build();

        try{
            String results = commonProxy.apply(param);
            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<StorageClass> clusterStorageClasss = mapper.readValue(results, new TypeReference<List<StorageClass>>(){});

            return clusterStorageClasss;

        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteStorageClass(Integer kubeConfigId, String name){
    	ClusterResourceInfo param = ClusterResourceInfo.builder().kubeConfigId(kubeConfigId).name(name).build();
        return nonNamespaceProxy.deleteResource(ResourceType.storageClass.get(), param);
    }
    
    public String getStorageClassYaml(Long kubeConfigId,String name) {

  		String storageClassYaml = nonNamespaceProxy.getResourceYaml(ResourceType.storageClass.get(), kubeConfigId,name);
  		return storageClassYaml;
  	}
    
    
}
