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

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngressDtoMapper {

	IngressDtoMapper INSTANCE = Mappers.getMapper(IngressDtoMapper.class);
	
	@Mapping(target = "namespaceIdx" , source = "namespace.id")
	@Mapping(target = "namespace" , source = "namespace.name")
	@Mapping(target = "ingressClass" , source = "ingressClass")
	@Mapping(target = "clusterName" , source = "namespace.cluster.clusterName")
	public IngressDto.ResListDto toResListDto(IngressEntity ingressEntity);
	
	@Mapping(target = "namespaceIdx" , source = "namespace.id")
	@Mapping(target = "namespace" , source = "namespace.name")
	@Mapping(target = "clusterId" , source = "namespace.cluster.clusterId")
	@Mapping(target = "clusterIdx" , source = "namespace.cluster.clusterIdx")
	@Mapping(target = "clusterName" , source = "namespace.cluster.clusterName")
	public IngressDto.ResDetailDto toResDetailDto(IngressEntity ingressEntity);
	
	
	@Mapping(target = "host" , source = "host", qualifiedByName = "hostEmpty")
	@Mapping(target = "endpoints" , source = "endpoint", qualifiedByName = "jsonToArray")
	public IngressDto.RuleList toRuleListDto(IngressRuleEntity ingressRuleEntity);
	
	@Named("hostEmpty")
    default String hostEmpty(String host) {
        if(host == null || host.length() == 0) {
        	return "-";
        }
        return host;
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
	
}
