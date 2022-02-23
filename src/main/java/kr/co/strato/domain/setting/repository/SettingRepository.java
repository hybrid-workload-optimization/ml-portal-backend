package kr.co.strato.domain.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.strato.domain.setting.model.SettingEntity;

public interface SettingRepository extends JpaRepository<SettingEntity, Long>, CustomSettingRepository,  JpaSpecificationExecutor<SettingEntity>{

}
