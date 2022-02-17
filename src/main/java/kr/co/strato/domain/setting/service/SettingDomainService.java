package kr.co.strato.domain.setting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.repository.SettingRepository;
import kr.co.strato.global.error.exception.InternalServerException;

@Service
public class SettingDomainService {
	
	@Autowired
	private SettingRepository settingRepository;
	
	//엔티티 조회(default) by id
	public SettingEntity getSetting(Long id) {
		return settingRepository.findById(id).get();
	}
	
	//엔티티 조회 by entity
	public SettingEntity getSetting(SettingEntity params) {
		return settingRepository.getSetting(params);
	}
	
	//엔티티 저장 및 수정
	public Long saveSetting(SettingEntity params) {
		settingRepository.save(params);
		return params.getSettingIdx();
	}
	
	//엔티티 일괄저장 및 수정
	public void saveAllSetting(List<SettingEntity> params) {
		try {
			settingRepository.saveAll(params);
		} catch (Throwable e) {
			throw new InternalServerException(e);
		}
	}
}
