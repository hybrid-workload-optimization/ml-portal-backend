package kr.co.strato.domain.job.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomJobRepositoryImpl  implements CustomJobRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomJobRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
