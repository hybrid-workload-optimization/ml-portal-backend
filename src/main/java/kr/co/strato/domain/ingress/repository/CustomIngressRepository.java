package kr.co.strato.domain.ingress.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface CustomIngressRepository {
    public Page<IngressEntity> getIngressList(Pageable pageable,String name,Long namespaceId);
}
