package kr.co.strato.adapter.k8s.job.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.batch.Job;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
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
public class JobAdapterService {
    @Autowired
    private CommonProxy commonProxy;

    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    public List<Job> create(Long clusterId, String yaml) {
    	yaml = Base64Util.decode(yaml);
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<Job> jobs = new ObjectMapper().readValue(results, new TypeReference<List<Job>>(){});
            return jobs;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - job 생성 에러");
        }
    }

    public List<Job> update(Long clusterId, String yaml){
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(clusterId).yaml(yaml).build();
        try{
            String results = commonProxy.apply(param);
            List<Job> statefulsets = new ObjectMapper().readValue(results, new TypeReference<List<Job>>(){});
            return statefulsets;

        }catch (FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Job 업데이트 에러");
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Job 업데이트 에러");
        }
    }

    public boolean delete(Long clusterId, String namespaceName, String jobName){
    	
        WorkloadResourceInfo reqBody = WorkloadResourceInfo.builder()
                .kubeConfigId(clusterId)
                .namespace(namespaceName)
                .name(jobName)
                .build();

        try{
            return inNamespaceProxy.deleteResource(ResourceType.job.get(), reqBody);    
        }catch (Exception e){
            throw new InternalServerException("k8s interface 통신 에러 - Job 삭제 에러");
        }
        
    }

    public Job retrieve(Long clusterId, String namespaceName, String jobName){
        try{
            String res = inNamespaceProxy.getResource(ResourceType.job.get(), clusterId, namespaceName, jobName);
            Job job = new ObjectMapper().readValue(res, new TypeReference<Job>(){});
            return job;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Job 조회 에러");
        }
    }

    public String getYaml(Long clusterId, String namespaceName, String jobName){
        try{
            String yaml = inNamespaceProxy.getResourceYaml(ResourceType.job.get(), clusterId, namespaceName, jobName);
            return yaml;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - Job yaml 조회 에러");
        }
    }
    
    public List<Job> getListFromOwnerUid(Long clusterId, String ownerUid) throws Exception {
		ResourceListSearchInfo body = ResourceListSearchInfo.builder()
				.kubeConfigId(clusterId)
				.ownerUid(ownerUid)
				.build();

		log.debug("[Get Job List] request : {}", body.toString());
		String response = inNamespaceProxy.getResourceList(ResourceType.job.get(), body);
		log.debug("[Get Job List] response : {}", response);

		ObjectMapper mapper = new ObjectMapper();
		List<Job> result = mapper.readValue(response, new TypeReference<List<Job>>(){});

		return result;
	}
}
