package kr.co.strato.adapter.k8s.cronjob.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.batch.CronJob;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CronJobAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    public List<CronJob> create(Long clusterId, String yaml) {
    	yaml = Base64Util.decode(yaml);
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<CronJob> cronJobs = new ObjectMapper().readValue(results, new TypeReference<List<CronJob>>(){});
            return cronJobs;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - cron job 생성 에러");
        }
    }

    public List<CronJob> update(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<CronJob> cronJobs = new ObjectMapper().readValue(results, new TypeReference<List<CronJob>>(){});
            return cronJobs;

        }catch (FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - cron job 업데이트 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - cron job 업데이트 에러");
        }
    }

    public boolean delete(Long clusterId, String namespaceName, String jobName){
    	
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(jobName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.cronJob.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - cron job 삭제 에러");
        }
        
    }

    public CronJob retrieve(Long clusterId, String namespaceName, String jobName){
        try{
            String res = inNamespaceProxy.getResource(ResourceType.cronJob.get(), clusterId, namespaceName, jobName);
            CronJob cronJob = new ObjectMapper().readValue(res, new TypeReference<CronJob>(){});
            return cronJob;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - cron job 조회 에러");
        }
    }

    public String getYaml(Long clusterId, String namespaceName, String jobName){
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.cronJob.get(), clusterId, namespaceName, jobName);
            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - cron job yaml 조회 에러");
        }
    }
}
