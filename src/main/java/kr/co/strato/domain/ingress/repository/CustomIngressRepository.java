package kr.co.strato.domain.ingress.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.ingress.model.IngressEntity;

public interface CustomIngressRepository {
    public Page<IngressEntity> getIngressList(Pageable pageable,Long clusterIdx,Long namespaceIdx);
    public void deleteIngress(Long ingressId);
    
    /**
     * IngressController를 사용하는 Ingress 리스트 반환.
     * @param ingressController
     * @return
     */
    public List<IngressEntity> getIngress(IngressControllerEntity ingressController);
    
    public IngressEntity getIngress(Long clusterIdx, String namespace, String name);
    
}
