package kr.co.strato.domain.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.setting.model.SettingEntity;

public interface SettingRepository extends JpaRepository<SettingEntity, Long>, CustomSettingRepository {


}
