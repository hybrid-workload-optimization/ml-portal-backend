package kr.co.strato.portal.workload.model;

import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.Period;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatefulSetDtoMapper {
    StatefulSetDtoMapper INSTANCE = Mappers.getMapper(StatefulSetDtoMapper.class);

    @Mapping(target = "name", source = "statefulSetName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "dayAgo", source = "createdAt", qualifiedByName = "getDayAgo")
    public StatefulSetDto.ResListDto toResListDto(StatefulSetEntity entity);

    @Named("getDayAgo")
    default String getDayAgo(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.between(createdAt.toLocalDate(), now.toLocalDate());

        return String.valueOf(period.getDays());
    }
}
