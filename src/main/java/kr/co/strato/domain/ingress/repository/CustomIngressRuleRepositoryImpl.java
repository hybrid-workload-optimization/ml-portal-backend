package kr.co.strato.domain.ingress.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;
import kr.co.strato.domain.ingress.model.QIngressEntity;
import kr.co.strato.domain.ingress.model.QIngressRuleEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomIngressRuleRepositoryImpl implements CustomIngressRuleRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomIngressRuleRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
    public List<IngressRuleEntity> findByIngressId(Long ingressId) {	
		QIngressRuleEntity qIngressRuleEntity = QIngressRuleEntity.ingressRuleEntity;
        QIngressEntity qIngressEntity = QIngressEntity.ingressEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(ingressId != null && ingressId > 0L){
            builder.and(qIngressRuleEntity.ingress.id.eq(ingressId));
        }

        QueryResults<IngressRuleEntity> results =
                jpaQueryFactory
                        .select(qIngressRuleEntity)
                        .from(qIngressRuleEntity)
                        .leftJoin(qIngressRuleEntity.ingress, qIngressEntity)
                        .where(builder)
                        .orderBy(qIngressRuleEntity.id.desc())
                        .fetchResults();
        List<IngressRuleEntity> content = results.getResults();

        return new ArrayList<>(content);
    }
    
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIngressRule(Long ingressId) {
    	  QIngressRuleEntity qIngressRuleEntity = QIngressRuleEntity.ingressRuleEntity;
    	  
    	  jpaQueryFactory.delete(qIngressRuleEntity)
          .where(qIngressRuleEntity.ingress.id.eq(ingressId))
          .execute();
    	  
    	  
    }

}
