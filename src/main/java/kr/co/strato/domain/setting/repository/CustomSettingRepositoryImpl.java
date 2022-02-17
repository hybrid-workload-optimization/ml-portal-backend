package kr.co.strato.domain.setting.repository;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.setting.model.SettingEntity;

import static kr.co.strato.domain.setting.model.QSettingEntity.settingEntity;

public class CustomSettingRepositoryImpl implements CustomSettingRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public CustomSettingRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}
	
	private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
		return Optional.ofNullable(value).map(function).orElse(null);
	}

	@Override
	public SettingEntity getSetting(SettingEntity params) {
		BooleanBuilder builder = new BooleanBuilder();
		if ( ObjectUtils.isNotEmpty(params) ) {
			if ( params.getSettingIdx() != null && params.getSettingIdx() > 0 ) {
				builder.and(condition(params.getSettingIdx(), settingEntity.settingIdx::eq));
			}
			
			if ( StringUtils.isNotEmpty(params.getSettingType()) ) {
				builder.and(condition(params.getSettingType(), settingEntity.settingType::eq));
			}
			
			if ( StringUtils.isNotEmpty(params.getSettingKey()) ) {
				builder.and(condition(params.getSettingKey(), settingEntity.settingKey::eq));
			}
			
			if ( StringUtils.isNotEmpty(params.getSettingValue()) ) {
				builder.and(condition(params.getSettingValue(), settingEntity.settingValue::eq));
			}
		}
		
		SettingEntity result = jpaQueryFactory.select(settingEntity)
			.from(settingEntity)
			.where(builder)
			.fetchFirst();
		
		return result;
	}
}
