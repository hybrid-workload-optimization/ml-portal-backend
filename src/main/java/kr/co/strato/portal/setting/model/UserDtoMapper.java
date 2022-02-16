package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserDtoMapper {
	
	UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);
}
