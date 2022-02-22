package kr.co.strato.adapter.k8s.statefulset.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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
    public List<StatefulSet> create(Integer clusterId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
//            System.out.println(results);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulsets = mapper.readValue(results, new TypeReference<List<StatefulSet>>(){});

            return statefulsets;

        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 생성 에러");
        }
    }

    public List<StatefulSet> update(Integer clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
//            System.out.println(results);

            //json -> fabric8 k8s 오브젝트 파싱
            ObjectMapper mapper = new ObjectMapper();
            List<StatefulSet> statefulsets = mapper.readValue(results, new TypeReference<List<StatefulSet>>(){});

            return statefulsets;

        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 업데이트 에러");
        }
    }

    /**
     * k8s 스테이트풀셋 삭제
     * @param clusterId
     * @param namespaceName
     * @param resourceName
     * @return
     */
    public boolean delete(Integer clusterId, String namespaceName, String resourceName){
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(resourceName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.statefulSet.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 삭제 에러");
        }
        
    }
}
