package kr.co.strato.domain.ingress.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.QIngressEntity;
import kr.co.strato.domain.ingress.model.QIngressRuleEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomIngressRepositoryImpl implements CustomIngressRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomIngressRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<IngressEntity> getIngressList(Pageable pageable,Long clusterIdx,Long namespaceIdx) {

        QIngressEntity qIngressEntity = QIngressEntity.ingressEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qCluster = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx != null && clusterIdx > 0L){
            builder.and(qCluster.clusterIdx.eq(clusterIdx));
        }
        if(namespaceIdx !=  null && namespaceIdx > 0L){
            builder.and(qNamespaceEntity.id.eq(namespaceIdx));
        }


        QueryResults<IngressEntity> results =
                jpaQueryFactory
                        .select(qIngressEntity)
                        .from(qIngressEntity)
                        .leftJoin(qIngressEntity.namespace, qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qCluster)
                        .where(builder)
                        .orderBy(qIngressEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<IngressEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
    
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIngress(Long ingressId) {
    	  QIngressEntity qIngressEntity = QIngressEntity.ingressEntity;
    	  QIngressRuleEntity qIngressRuleEntity = QIngressRuleEntity.ingressRuleEntity;
    	  
    	  jpaQueryFactory.delete(qIngressRuleEntity)
          .where(qIngressRuleEntity.ingress.id.eq(ingressId))
          .execute();
    	  
    	  
          jpaQueryFactory.delete(qIngressEntity)
          .where(qIngressEntity.id.eq(ingressId))
          .execute();
    	  
    }
    
    @Override
	public List<IngressEntity> getIngress(IngressControllerEntity ingressController) {
        QIngressEntity qIngressEntity = QIngressEntity.ingressEntity;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder builderIngressClass = new BooleanBuilder();
        builder.and(qIngressEntity.cluster.clusterIdx.eq(ingressController.getCluster().getClusterIdx()));
        builder.and(builderIngressClass);
        
        
        builderIngressClass.and(qIngressEntity.ingressClass.eq(ingressController.getIngressClass()));
        if(ingressController.getDefaultYn().equals("Y")) {
        	builderIngressClass.or(qIngressEntity.ingressClass.eq("default"));
        }

        QueryResults<IngressEntity> results =
                jpaQueryFactory
                        .select(qIngressEntity)
                        .from(qIngressEntity)
                        .where(builder)
                        .fetchResults();

        List<IngressEntity> content = results.getResults();
        return content;
	}

}
