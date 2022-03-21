package kr.co.strato.domain.code.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.code.model.CodeEntity;
import kr.co.strato.domain.code.model.GroupCodeEntity;
import kr.co.strato.domain.code.repository.CodeRepository;
import kr.co.strato.domain.code.repository.GroupCodeRepository;
import kr.co.strato.global.error.exception.AlreadyExistResourceException;

@Service
public class CodeMgmtDomainService {

	@Autowired
	GroupCodeRepository groupCodeRepository;

	@Autowired
	CodeRepository codeRepository;
	
	public List<GroupCodeEntity> getGroupCodeList() {
		return groupCodeRepository.findByUseYn("Y");
	}
	
	public GroupCodeEntity getGroupCode(String groupCode) {
		return groupCodeRepository.findById(groupCode).get();
	}
	
	public String saveGroupCode(GroupCodeEntity groupCode) {
		if (groupCodeRepository.existsById(groupCode.getGroupCode())) {
			throw new AlreadyExistResourceException();
		}
		groupCodeRepository.save(groupCode);
		return groupCode.getGroupCode();
	}

	public String updateGroupCode(GroupCodeEntity groupCode) {
		groupCodeRepository.save(groupCode);
		return groupCode.getGroupCode();
	}
	
	public CodeEntity getCode(Long codeIdx) {
		return codeRepository.findById(codeIdx).get();
	}

	public Long saveCode(CodeEntity code) {
		if (codeRepository.existsByGroupCodeAndCommonCode(code.getGroupCode(), code.getCommonCode())) {
			throw new AlreadyExistResourceException();
		}
		codeRepository.save(code);
		return code.getCodeIdx();
	}
	
	public Long updateCode(CodeEntity code) {
//		if (!codeRepository.existsById(code.getCodeIdx())) {
//			throw new NotFoundResourceException();
//		}
		codeRepository.save(code);
		return code.getCodeIdx();
	}

}
