package kr.co.strato.domain.service.service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceDomainService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;


    public Page<ServiceEntity> getServices(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        return serviceRepository.getServices(pageable, projectIdx, clusterIdx, namespaceIdx);
    }

    public Long register(ServiceEntity service, ClusterEntity cluster, String namespaceName){
        NamespaceEntity namespace = getNamespace(cluster, namespaceName);
        service.setNamespace(namespace);
        serviceRepository.save(service);

        return service.getId();
    }

    private NamespaceEntity getNamespace(ClusterEntity cluster, String namespaceName){
        List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);

        if(namespaces != null && namespaces.size() > 0){
            return namespaces.get(0);
        }

        return null;
    }
}
