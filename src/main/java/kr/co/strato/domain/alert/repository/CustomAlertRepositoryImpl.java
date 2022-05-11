package kr.co.strato.domain.alert.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.alert.model.AlertEntity;
import kr.co.strato.domain.alert.model.QAlertEntity;

public class CustomAlertRepositoryImpl implements CustomAlertRepository {
	
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomAlertRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public List<AlertEntity> getAlerts(String userId) {
		QAlertEntity qAlertEntiry = QAlertEntity.alertEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if (userId != null && userId.length() > 0L) {
        	 builder.and(qAlertEntiry.userId.eq(userId));
        }
        
        //일주일 전부터 현재시간 까지의 알람 데이터만 조회
        LocalDateTime now = LocalDateTime.now(); 
        LocalDateTime before = LocalDateTime.now().minusDays(7);
        
        builder.and(qAlertEntiry.createdAt.between(before, now));

        QueryResults<AlertEntity> results = jpaQueryFactory
        		.select(qAlertEntiry)
                .from(qAlertEntiry)
                .where(builder)
                .orderBy(qAlertEntiry.createdAt.desc())
                .fetchResults();

        List<AlertEntity> content = results.getResults();
		return content;
	}

}
