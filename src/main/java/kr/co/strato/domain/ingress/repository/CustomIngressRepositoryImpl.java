package kr.co.strato.domain.ingress.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.QIngressEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomIngressRepositoryImpl implements CustomIngressRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomIngressRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<IngressEntity> getIngressList(Pageable pageable,String name,Long namespaceId) {

        QIngressEntity qIngressEntity = QIngressEntity.ingressEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(namespaceId != null && namespaceId > 0L){
            builder.and(qNamespaceEntity.id.eq(namespaceId));
        }

        QueryResults<IngressEntity> results =
                jpaQueryFactory
                        .select(qIngressEntity)
                        .from(qIngressEntity)
                        .leftJoin(qIngressEntity.namespace, qNamespaceEntity)
                        .where(builder)
                        .orderBy(qIngressEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<IngressEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

}
