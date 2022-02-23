package kr.co.strato.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.user.model.UserEntity;

public class CustomUserRepositoryImpl implements CustomUserRepository {
	
	private final JPAQueryFactory jpaQueryFactory;

	public CustomUserRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

}
