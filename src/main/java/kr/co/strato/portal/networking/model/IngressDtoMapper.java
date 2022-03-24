package kr.co.strato.portal.networking.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngressDtoMapper {

	IngressDtoMapper INSTANCE = Mappers.getMapper(IngressDtoMapper.class);
	
	@Mapping(target = "namespaceIdx" , source = "namespace.id")
	@Mapping(target = "namespace" , source = "namespace.name")
	@Mapping(target = "ingressControllerIdx" , source = "ingressController.id")
	@Mapping(target = "address" , source = "ingressController.address")
	@Mapping(target = "host" , source = "ingressController.name")
	public IngressDto.ResListDto toResListDto(IngressEntity ingressEntity);
	
	@Mapping(target = "namespaceIdx" , source = "namespace.id")
	@Mapping(target = "namespace" , source = "namespace.name")
	@Mapping(target = "clusterId" , source = "namespace.cluster.clusterId")
	@Mapping(target = "ingressControllerIdx" , source = "ingressController.id")
	@Mapping(target = "address" , source = "ingressController.address")
	@Mapping(target = "host" , source = "ingressController.name")
	public IngressDto.ResDetailDto toResDetailDto(IngressEntity ingressEntity);
	
	
	public IngressDto.RuleList toRuleListDto(IngressRuleEntity ingressRuleEntity);
	
}
