package kr.co.strato.portal.networking.model;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.k8s.ingressController.model.CreateIngressControllerParam;
import kr.co.strato.adapter.k8s.ingressController.model.ServicePort;
import kr.co.strato.domain.IngressController.model.IngressControllerEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngressControllerDtoMapper {

	IngressControllerDtoMapper INSTANCE = Mappers.getMapper(IngressControllerDtoMapper.class);
	
	
	@Mapping(target = "externalIp",	 source = "externalIp",			qualifiedByName = "jsonToArray")
	@Mapping(target = "port",		 source = "port", 				qualifiedByName = "jsonToPort")
	@Mapping(target = "default",	 source = "defaultYn", 		qualifiedByName = "stringToBoolean")
	@Mapping(target = "clusterIdx",	 source = "cluster.clusterIdx")
	@Mapping(target = "clusterName",	 source = "cluster.clusterName")
	public IngressControllerDto.ResListDto toResListDto(IngressControllerEntity ingressEntity);
	
	@Mapping(target = "externalIp",	 source = "dto.externalIp",		qualifiedByName = "ipsToString")
	@Mapping(target = "port",		 source = "dto.port", 			qualifiedByName = "portToString")
	@Mapping(target = "defaultYn",   source = "dto.default", 		qualifiedByName = "booleanToString")
	@Mapping(target = "cluster.clusterIdx",   source = "clusterIdx")
	public IngressControllerEntity toEntity(IngressControllerDto.ResListDto dto);
	
	@Mapping(target = "externalIp",	 source = "dto.externalIp",		qualifiedByName = "ipsToString")
	@Mapping(target = "port",		 source = "dto", 			qualifiedByName = "dtoToPort")
	@Mapping(target = "defaultYn",   source = "dto.default", 		qualifiedByName = "booleanToString")
	@Mapping(target = "cluster.clusterIdx",   source = "clusterIdx")
	public IngressControllerEntity toEntity(IngressControllerDto.ReqCreateDto dto, Long clusterIdx);
	
	
	@Mapping(target = "ingressControllerType",	 source = "dto.name")
	@Mapping(target = "kubeConfigId",	 source = "kubeConfigId")
	public CreateIngressControllerParam toCreateParam(IngressControllerDto.ReqCreateDto dto, Long kubeConfigId);
	
	@Named("dtoToPort")
    default String dtoToPort(IngressControllerDto.ReqCreateDto dto) {
        Integer http =  dto.getHttpPort();
        Integer https = dto.getHttpsPort();
        
        List<ServicePort> list = new ArrayList<>();
        if(http != null && http > 0) {
        	ServicePort httpPort = new ServicePort();
            httpPort.setProtocol("http");
            httpPort.setPort(http);
            list.add(httpPort);
        }
        
        if(https != null && https > 0) {
        	ServicePort httpsPort = new ServicePort();
        	httpsPort.setProtocol("https");
        	httpsPort.setPort(https);
        	list.add(httpsPort);
        }
        ObjectMapper mapper = new ObjectMapper();
		String result = null;
		try {
			result = mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
    }
	
	
	@Named("jsonToArray")
    default List<String> jsonToArray(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> list = mapper.readValue(json, new TypeReference<List<String>>() {});

            return list;
        } catch (JsonProcessingException e) {
            return new ArrayList<String>();
        }
    }
	
	@Named("jsonToPort")
    default List<ServicePort> jsonToPort(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ServicePort> list = mapper.readValue(json, new TypeReference<List<ServicePort>>() {});

            return list;
        } catch (JsonProcessingException e) {
            return new ArrayList<ServicePort>();
        }
    }
	
	@Named("ipsToString")
	default String ipsToString(List<String> list){

		try{
			ObjectMapper mapper = new ObjectMapper();
			String result = mapper.writeValueAsString(list);
			return result;
		}catch (JsonProcessingException e){
			return null;
		}
	}
	
	@Named("portToString")
	default String portToString(List<ServicePort> list){

		try{
			ObjectMapper mapper = new ObjectMapper();
			String result = mapper.writeValueAsString(list);
			return result;
		}catch (JsonProcessingException e){
			return null;
		}
	}
	
	
	
	@Named("stringToBoolean")
	default boolean stringToBoolean(String yn){
		return yn.toLowerCase().equals("y")? true:false;
	}
	
	@Named("booleanToString")
	default String booleanToString(boolean b){
		return b? "Y":"N";
	}
}
