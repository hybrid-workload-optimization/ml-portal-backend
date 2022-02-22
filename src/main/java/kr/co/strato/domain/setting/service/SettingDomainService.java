package kr.co.strato.domain.setting.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.repository.SettingRepository;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class SettingDomainService {
	
	@Autowired
	private SettingRepository settingRepository;
	
	//엔티티 조회(default) by id
	public SettingEntity getSetting(Long id) {
		Optional<SettingEntity> setting = settingRepository.findById(id);
		if (setting.isPresent()) {
			return setting.get();
		} else {
			throw new NotFoundResourceException("setting_idx : " + id);
		}
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
	
	
	//간단해보이긴하는데 이거 가독성 구리다고 쓰지 말라고함. getSettings를 하나 만들어서 쓰는게 나아보임.
	public List<SettingEntity> findSettingEntitiesByType(String settingType){
		Specification<SettingEntity> spec = SettingEntitySpecs.equalsSettingType(settingType);
		return settingRepository.findAll(spec);
	}
	
	static class SettingEntitySpecs {
		@SuppressWarnings("serial")
		public static Specification<SettingEntity> equalsSettingType(String settingType) {
			return new Specification<SettingEntity>() {
				@Override
				public Predicate toPredicate(Root<SettingEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
					return criteriaBuilder.equal(root.get("settingType"), settingType);
				}
			};
		}
	}
}


