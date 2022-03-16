package kr.co.strato.adapter.k8s.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import feign.FeignException;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.fabric8.kubernetes.api.model.Service;

import java.util.List;

@org.springframework.stereotype.Service
@Slf4j
public class ServiceAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    /**
     * 서비스 생성
     * @param clusterId
     * @param yaml
     * @return
     */
    public List<Service> create(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            ObjectMapper mapper = new ObjectMapper();

            Gson gson = new GsonBuilder().create();
            List<Service> services = gson.fromJson(results, new TypeToken<List<Service>>(){}.getType());
            return services;
        }catch(FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
        }catch(JsonParseException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
        }
    }

    /**
     * 서비스 업데이트
     * @param clusterId
     * @param yaml
     * @return
     */
    public List<Service> update(Long clusterId, String yaml){
        System.out.println("yaml~~~:"+yaml);
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);

            ObjectMapper mapper = new ObjectMapper();
            List<Service> services = mapper.readValue(results, new TypeReference<List<Service>>() {});

            return services;
        }catch(FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service 업데이트 에러");
        }catch(JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service 업데이트 에러");
        }
    }

    /**
     * 서비스 삭제 
     * @param clusterId
     * @param namespaceName
     * @param serviceName
     * @return
     */
    public boolean delete(Long clusterId, String namespaceName, String serviceName){
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(serviceName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.service.get(), reqBody);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - statefulSet 삭제 에러");
        }
    }

    /**
     * 서비스 조회
     * @param clusterId
     * @param namespaceName
     * @param serviceName
     * @return
     */
    public Service get(Long clusterId, String namespaceName, String serviceName){
        try{
            String result = inNamespaceProxy.getResource(ResourceType.service.get(), clusterId, namespaceName, serviceName);

            ObjectMapper mapper = new ObjectMapper();
            Service service = mapper.readValue(result, new TypeReference<Service>() {});

            return service;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service 조회 에러");
        }
    }

    /**
     * 서비스 yaml 조회
     * @param clusterId
     * @param namespaceName
     * @param serviceName
     * @return
     */
    public String getYaml(Long clusterId, String namespaceName, String serviceName){
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.service.get(), clusterId, namespaceName, serviceName);
//            System.out.println("clusterId:"+clusterId);
//            System.out.println("namespaceName:"+namespaceName);
//            System.out.println("serviceName:"+serviceName);
//            System.out.println("yaml:"+yaml);
            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - service yaml 조회 에러");
        }
    }
}
