package kr.co.strato.adapter.k8s.ingress.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.adapter.k8s.common.proxy.NonNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IngressAdapterService {
	@Autowired
	private InNamespaceProxy inNamespaceProxy;
	
	@Autowired
	private NonNamespaceProxy nonNamespaceProxy;
	
    @Autowired
    private CommonProxy commonProxy;

    /**
     * @param kubeConfigId
     * @return
     * @throws JsonProcessingException
     */
    public List<Ingress> getIngressList(Long kubeConfigId) {
		// 요청 파라미터 객체 생성
		ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(kubeConfigId).build();

		// 조회 요청
		String results = inNamespaceProxy.getResourceList(ResourceType.ingress.get(), param);

		try {
			// json -> fabric8 k8s 오브젝트 파싱
			ObjectMapper mapper = new ObjectMapper();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<Ingress> ingress = gson.fromJson(results, new TypeToken<List<Ingress>>() {}.getType());

//			ObjectMapper mapper = new ObjectMapper();
//			List<Ingress> ingress = mapper.readValue(results, new TypeReference<List<Ingress>>() {});

			return ingress;
		} catch (FeignException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
		} catch (JsonParseException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
		}
	}
    
    
    public List<Ingress> registerIngress(Long kubeConfigId, String yaml) {
        YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yaml).build();

        try{
            String results = commonProxy.apply(param);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<Ingress> ingress = gson.fromJson(results, new TypeToken<List<Ingress>>() {
			}.getType());

//			ObjectMapper mapper = new ObjectMapper();
//			List<Ingress> ingress = mapper.readValue(results, new TypeReference<List<Ingress>>() {});

			return ingress;
		} catch (FeignException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
		} catch (JsonParseException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("k8s interface 통신 에러 - service 생성 에러");
		}
    }

    public boolean deleteIngress(Long kubeConfigId, String name){
    	WorkloadResourceInfo param = WorkloadResourceInfo.builder().kubeConfigId(kubeConfigId).name(name).build();
        return inNamespaceProxy.deleteResource(ResourceType.ingress.get(), param);
    }
    
    public String getIngressYaml(Long kubeConfigId,String name, String namespace) {
  		String ingressYaml = inNamespaceProxy.getResourceYaml(ResourceType.ingress.get(), kubeConfigId, namespace,name);
  		return ingressYaml;
  	}
    
    public List<Ingress> getIngressClassName(Long kubeConfigId,String name) {
		// 요청 파라미터 객체 생성
		ResourceListSearchInfo param = ResourceListSearchInfo.builder().kubeConfigId(kubeConfigId).name(name).build();

		// 조회 요청
		String results = nonNamespaceProxy.getResourceList(ResourceType.ingressClass.get(), param);

		try {
			// json -> fabric8 k8s 오브젝트 파싱
			ObjectMapper mapper = new ObjectMapper();
			List<Ingress> ingress = mapper.readValue(results, new TypeReference<List<Ingress>>() {
			});

			return ingress;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw new InternalServerException("json 파싱 에러");
		}
	}
    
}
