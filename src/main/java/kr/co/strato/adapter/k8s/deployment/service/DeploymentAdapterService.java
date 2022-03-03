package kr.co.strato.adapter.k8s.deployment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DeploymentAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    public List<Deployment> create(Long clusterId, String yaml) {
    	yaml = Base64Util.decode(yaml);
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<Deployment> deployments = new ObjectMapper().readValue(results, new TypeReference<List<Deployment>>(){});
            return deployments;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Deployment 생성 에러");
        }
    }

    public List<Deployment> update(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<Deployment> statefulsets = new ObjectMapper().readValue(results, new TypeReference<List<Deployment>>(){});
            return statefulsets;

        }catch (FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Deployment 업데이트 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Deployment 업데이트 에러");
        }
    }

    public boolean delete(Long clusterId, String namespaceName, String deploymentName){
    	Integer iClusterId = null;
    	if(clusterId != null)
    		iClusterId = Long.valueOf(clusterId).intValue();
    	
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(iClusterId)
                .namespace(namespaceName)
                .name(deploymentName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.deployment.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - Deployment 삭제 에러");
        }
        
    }

    public Deployment retrieve(Long clusterId, String namespaceName, String deploymentName){
    	Integer iClusterId = null;
    	if(clusterId != null)
    		iClusterId = Long.valueOf(clusterId).intValue();
    	
        try{
            String res = inNamespaceProxy.getResource(ResourceType.deployment.get(), iClusterId, namespaceName, deploymentName);
            Deployment deployment = new ObjectMapper().readValue(res, new TypeReference<Deployment>(){});
            return deployment;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Deployment 조회 에러");
        }
    }

    public String getYaml(Long clusterId, String namespaceName, String deploymentName){
    	Integer iClusterId = null;
    	if(clusterId != null)
    		iClusterId = Long.valueOf(clusterId).intValue();
    	
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.deployment.get(), iClusterId, namespaceName, deploymentName);
            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Deployment yaml 조회 에러");
        }
    }
}
