package kr.co.strato.portal.setting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.code.model.CodeEntity;
import kr.co.strato.domain.code.model.GroupCodeEntity;
import kr.co.strato.domain.code.service.CodeMgmtDomainService;
import kr.co.strato.portal.setting.model.CodeDto;
import kr.co.strato.portal.setting.model.CodeDtoMapper;
import kr.co.strato.portal.setting.model.GroupCodeDto;
import kr.co.strato.portal.setting.model.GroupCodeDtoMapper;

@Service
public class CodeMgmtService {

	@Autowired
	CodeMgmtDomainService codeDomainService;
	
	public List<GroupCodeDto> getGroupCodeList() {
		List<GroupCodeEntity> entityList = codeDomainService.getGroupCodeList();
		List<GroupCodeDto> dtoList = entityList.stream().map(o -> GroupCodeDtoMapper.INSTANCE.toDto(o)).collect(Collectors.toList());
		return dtoList;
	}
	
	public GroupCodeDto getGroupCode(String groupCode) {
		GroupCodeEntity entity = codeDomainService.getGroupCode(groupCode);
		GroupCodeDto dto = GroupCodeDtoMapper.INSTANCE.toDto(entity);
		return dto;
	}
		
	public String saveGroupCode(GroupCodeDto dto) {
		GroupCodeEntity entity = GroupCodeDtoMapper.INSTANCE.toEntity(dto);
		return codeDomainService.saveGroupCode(entity);
	}

	public String updateGroupCode(GroupCodeDto dto) {
		GroupCodeEntity entity = GroupCodeDtoMapper.INSTANCE.toEntity(dto);
		return codeDomainService.updateGroupCode(entity);
	}

	public CodeDto getCode(Long codeIdx) {
		CodeEntity entity = codeDomainService.getCode(codeIdx);
		CodeDto dto = CodeDtoMapper.INSTANCE.toDto(entity);
		return dto;
	}
	
	public Long saveCode(CodeDto dto) {
		CodeEntity entity = CodeDtoMapper.INSTANCE.toEntity(dto);
		return codeDomainService.saveCode(entity);
	}
	

	public Long updateCode(CodeDto dto) {
		CodeEntity entity = CodeDtoMapper.INSTANCE.toEntity(dto);
		return codeDomainService.updateCode(entity);
	}

}
