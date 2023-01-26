package kr.co.strato.portal.networking.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Service;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface K8sServiceOnlyApiDtoMapper {
    K8sServiceOnlyApiDtoMapper INSTANCE = Mappers.getMapper(K8sServiceOnlyApiDtoMapper.class);

    @Named("stringToMap")
    default HashMap<String, Object> stringToMap(String text){
        if(text == null){
            return null;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(text, HashMap.class);
            return map;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    @Named("stringToMapList")
    default List<HashMap<String, Object>> stringToMapList(String text){
        if(text == null){
            return null;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> maps = mapper.readValue(text, new TypeReference<List<HashMap<String, Object>>>() {
            });
            return maps;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
    
    default Object toDto(Service s, boolean isDefault) {
    	try {		
    		Gson gson = new GsonBuilder().create();
	        String uid = s.getMetadata().getUid();
	        String name = s.getMetadata().getName();
	        String namespace = s.getMetadata().getNamespace();
	        LocalDateTime createAt = DateUtil.strToLocalDateTime(s.getMetadata().getCreationTimestamp());
	        String type = s.getSpec().getType();
	        String clusterIp = s.getSpec().getClusterIP();
	        String sessionAffinity = s.getSpec().getSessionAffinity();
	        String selector = gson.toJson(s.getSpec().getSelector());
	        String annotation = gson.toJson(s.getMetadata().getAnnotations());
	        String label = gson.toJson(s.getMetadata().getLabels());
	        String internalEndPoint = null;
	        String externalEndPoint = null;
	        internalEndPoint = gson.toJson(s.getSpec().getPorts());
	        
	        if(isDefault) {
	        	K8sServiceOnlyApiDto.ResDetailDto detail = K8sServiceOnlyApiDto.ResDetailDto.builder()
	        			.serviceName(name)
	        			.namespaceName(namespace)
	        			.label(stringToMap(label))
	        			.internalEndpoints(stringToMapList(internalEndPoint))
	        			.externalEndpoints(stringToMapList(externalEndPoint))
	        			.type(type)
	        			.selector(stringToMap(selector))
	        			.sessionAffinity(sessionAffinity)
	        			.serviceUid(uid)
	        			.annotation(stringToMap(annotation))
	        			.clusterIp(clusterIp)
	        			.createdAt(createAt)
	        			.build();
	        	return detail;
	        } else {
	        	K8sServiceOnlyApiDto.ResListDto list = K8sServiceOnlyApiDto.ResListDto.builder()
	        			.name(name)
	        			.namespace(namespace)
	        			.label(stringToMap(label))
	        			.internalEndpoints(stringToMapList(internalEndPoint))
	        			.externalEndpoints(stringToMapList(externalEndPoint))
	        			.type(type)
	        			.age(createAt)
	        			.build();
	        	return list;
	        }    	        
    	} catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s Service model to Dto");
        }
    }
}
