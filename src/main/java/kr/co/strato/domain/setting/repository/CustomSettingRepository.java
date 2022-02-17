package kr.co.strato.domain.setting.repository;

import kr.co.strato.domain.setting.model.SettingEntity;

public interface CustomSettingRepository {
	SettingEntity getSetting(SettingEntity params);
}
