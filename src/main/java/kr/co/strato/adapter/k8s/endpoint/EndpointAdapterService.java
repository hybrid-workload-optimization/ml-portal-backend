package kr.co.strato.adapter.k8s.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import feign.FeignException;
import io.fabric8.kubernetes.api.model.Endpoints;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.common.proxy.InNamespaceProxy;
import kr.co.strato.global.error.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EndpointAdapterService {
    @Autowired
    private InNamespaceProxy inNamespaceProxy;

    public Endpoints get(Long clusterId, String namespaceName, String endpointName){
        try{
            String result = inNamespaceProxy.getResource(ResourceType.endpoints.get(), clusterId, namespaceName, endpointName);
            Gson gson = new GsonBuilder().create();
            Endpoints endpoints = gson.fromJson(result,  Endpoints.class);

            return endpoints;
        }catch(FeignException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - endpoint 조회 에러");
        }catch (JsonParseException e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("json 파싱 에러");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new InternalServerException("k8s interface 통신 에러 - endpoint 조회 에러");
        }
    }
}
