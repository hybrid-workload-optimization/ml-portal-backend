package kr.co.strato.adapter.k8s.statefulset.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class StatefulSetAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    /**
     * k8s 스테이트풀셋 생성
     * @param clusterId
     * @param yaml
     * @return
     */
    public List<StatefulSet> create(Long clusterId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulsets = mapper.readValue(results, new TypeReference<List<StatefulSet>>(){});

            return statefulsets;
        }catch (FeignException e){
            log.error(e.getMessage(), e);
//            System.out.println(e.request());
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 생성 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 생성 에러");
        }
    }

    public List<StatefulSet> update(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
//            System.out.println(results);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulsets = mapper.readValue(results, new TypeReference<List<StatefulSet>>(){});

            return statefulsets;

        }catch (FeignException e){
            log.error(e.getMessage(), e);
//            System.out.println(e.request());
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 업데이트 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 업데이트 에러");
        }
    }

    /**
     * 스테이트풀셋 삭제
     * @param clusterId
     * @param namespaceName
     * @param statefulSetName
     * @return
     */
    public boolean delete(Long clusterId, String namespaceName, String statefulSetName){
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(statefulSetName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.statefulSet.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 삭제 에러");
        }
        
    }

    /**
     * 스테이트풀셋 조회
     * @param clusterId
     * @param namespaceName
     * @param statefulSetName
     * @return
     */
    public StatefulSet get(Long clusterId, String namespaceName, String statefulSetName){
        try{
            String result = inNamespaceProxy.getResource(ResourceType.statefulSet.get(), clusterId, namespaceName, statefulSetName);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            StatefulSet statefulSet = mapper.readValue(result, new TypeReference<StatefulSet>(){});

            return statefulSet;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 조회 에러");
        }
    }

    /**
     * 스테이트풀셋 yaml 조회
     * @param clusterId
     * @param namespaceName
     * @param statefulSetName
     * @return
     */
    public String getYaml(Long clusterId, String namespaceName, String statefulSetName){
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.statefulSet.get(), clusterId, namespaceName, statefulSetName);

            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet yaml 조회 에러");
        }
    }

    /**
     * 스테이트풀셋 리스트 조회
     * @param clusterId
     * @return
     */
   public List<StatefulSet> getList(Long clusterId){
       try{
           ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(clusterId).build();
            String result = inNamespaceProxy.getResourceList(ResourceType.statefulSet.get(), param);

            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulSets = mapper.readValue(result, new TypeReference<List<StatefulSet>>(){});

            return statefulSets;
       }catch (JsonProcessingException e){
           log.error(e.getMessage(), e);
           throw new InternalServerException("json 파싱 에러");
       }catch (Exception e){
           log.error(e.getMessage(), e);
           throw new InternalServerException("k8s interface 통신 에러 - statefulSet list 조회 에러");
       }
    }

    /**
     * 스테이트풀셋 리스트 조회
     * @param clusterId
     * @param namespaceName
     * @return
     */
    public List<StatefulSet> getList(Long clusterId, String namespaceName){
        try{
            ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(clusterId)
                    .namespace(namespaceName).build();
            String result = inNamespaceProxy.getResourceList(ResourceType.statefulSet.get(), param);

            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulSets = mapper.readValue(result, new TypeReference<List<StatefulSet>>(){});

            return statefulSets;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet list 조회 에러");
        }
    }
    
    public List<StatefulSet> getListFromOwnerUid(Long clusterId, String ownerUid) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.ownerUid(ownerUid)
				.build();

		log.debug("[Get Job List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.statefulSet.get(), body);
		log.debug("[Get Job List] response : {}", response);

		ObjectMapper mapper = new ObjectMapper();
		List<StatefulSet> result = mapper.readValue(response, new TypeReference<List<StatefulSet>>(){});

		return result;
	}
}
