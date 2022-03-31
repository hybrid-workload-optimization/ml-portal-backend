package kr.co.strato.portal.config.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersistentVolumeClaimDtoMapper {
    PersistentVolumeClaimDtoMapper INSTANCE = Mappers.getMapper(PersistentVolumeClaimDtoMapper.class);

    @Mappings({
        @Mapping(target = "namespace", source = "namespace.name"),
    })
    public PersistentVolumeClaimDto.ResListDto toResListDto(PersistentVolumeClaimEntity entity);
    
}
