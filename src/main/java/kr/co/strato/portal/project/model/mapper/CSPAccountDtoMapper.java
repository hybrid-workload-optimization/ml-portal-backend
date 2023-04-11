package kr.co.strato.portal.project.model.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.adapter.sso.model.dto.CSPAccountDTO;
import kr.co.strato.portal.project.model.CSPAccountDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CSPAccountDtoMapper {

	CSPAccountDtoMapper INSTANCE = Mappers.getMapper(CSPAccountDtoMapper.class);

	@Named("cspDto")
	@Mapping(target = "accountId", 	source = "accountData", 	qualifiedByName = "accountId")
    public CSPAccountDto toDto(CSPAccountDTO dto);
	
	
	@IterableMapping(qualifiedByName = "cspDto")
    public List<CSPAccountDto> dtoList(List<CSPAccountDTO> dtoList);
	
	
	@Named("accountId")
	default String accountId(Map<String, String> accountData) {
		return accountData.get("accountId");
	}
}
