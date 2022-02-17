package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.portal.setting.model.GeneralDto;
import kr.co.strato.domain.setting.service.SettingDomainService;

@Service
public class GeneralService {
	
	@Autowired
	SettingDomainService SettingDomainService;
	
	public List<GeneralDto> getList(){
		return new ArrayList<GeneralDto>();
	}
}
