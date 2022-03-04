package kr.co.strato.domain.cronjob.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomCronJobRepositoryImpl  implements CustomCronJobRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomCronJobRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
