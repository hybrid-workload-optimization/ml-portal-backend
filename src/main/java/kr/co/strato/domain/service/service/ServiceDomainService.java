package kr.co.strato.domain.service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.service.model.ServiceEndpointEntity;
import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.repository.ServiceEndPointRepository;
import kr.co.strato.domain.service.repository.ServiceRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class ServiceDomainService implements InNamespaceDomainService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;

    @Autowired
    private ServiceEndPointRepository serviceEndPointRepository;


    public Page<ServiceEntity> getServices(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        return serviceRepository.getServices(pageable, projectIdx, clusterIdx, namespaceIdx);
    }

    public Long register(ServiceEntity service, List<ServiceEndpointEntity> serviceEndpoints, ClusterEntity cluster, String namespaceName){
        NamespaceEntity namespace = getNamespace(cluster, namespaceName);
        service.setNamespace(namespace);
        serviceRepository.save(service);

        if(serviceEndpoints != null){
            for(ServiceEndpointEntity endpoint : serviceEndpoints){
                registerServiceEndpoint(endpoint,service);
            }
        }

        return service.getId();
    }

    /**
     * 서비스 전체 업데이트(id, namespace 제외)
     * @param serviceId 업데이트 될 엔티티의 아이디
     * @param updateEntity 업데이트 할 새로운 데이터 엔티티
     * @return
     */
    public Long update(Long serviceId, ServiceEntity updateEntity, List<ServiceEndpointEntity> serviceEndpoints){
        ServiceEntity oldEntity = get(serviceId);
        changeToNewData(oldEntity, updateEntity);
        serviceRepository.save(oldEntity);

        deleteServiceEndpoints(oldEntity);

        if(serviceEndpoints != null){
            for(ServiceEndpointEntity endpoint : serviceEndpoints){
                registerServiceEndpoint(endpoint,oldEntity);
            }
        }
        return oldEntity.getId();
    }

    public ClusterEntity getClusterEntity(Long serviceId){
        ServiceEntity serviceEntity = get(serviceId);

        return serviceEntity.getNamespace().getCluster();
    }

    public ServiceEntity get(Long serviceId){
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundResourceException("service id:"+serviceId));

        return serviceEntity;
    }

    public boolean delete(Long serviceId){
        Optional<ServiceEntity> opt = serviceRepository.findById(serviceId);
        if(opt.isPresent()){
            ServiceEntity entity = opt.get();
            delete(entity);
        }
        return true;
    }
    
    public void delete(ServiceEntity entity) { 
    	deleteServiceEndpoints(entity);
        serviceRepository.delete(entity);
    }

    public List<ServiceEndpointEntity> getServiceEndpoints(Long serviceId){
        ServiceEntity service = ServiceEntity.builder().id(serviceId).build();
        return serviceEndPointRepository.findByService(service);
    }

    private NamespaceEntity getNamespace(ClusterEntity cluster, String namespaceName){
        List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);
        if(namespaces != null && namespaces.size() > 0){
            return namespaces.get(0);
        }

        return null;
    }

    private void changeToNewData(ServiceEntity oldEntity, ServiceEntity newEntity){
        oldEntity.setServiceUid(newEntity.getServiceUid());
        oldEntity.setCreatedAt(newEntity.getCreatedAt());
        oldEntity.setType(newEntity.getType());
        oldEntity.setClusterIp(newEntity.getClusterIp());
        oldEntity.setSessionAffinity(newEntity.getSessionAffinity());
        oldEntity.setInternalEndpoint(newEntity.getInternalEndpoint());
        oldEntity.setExternalEndpoint(newEntity.getExternalEndpoint());
        oldEntity.setSelector(newEntity.getSelector());
        oldEntity.setAnnotation(newEntity.getAnnotation());
        oldEntity.setLabel(newEntity.getLabel());
    }

    private void deleteServiceEndpoints(ServiceEntity service){
        serviceEndPointRepository.deleteByService(service);
    }

    private void registerServiceEndpoint(ServiceEndpointEntity serviceEndpoint, ServiceEntity service){
        serviceEndpoint.setService(service);
        serviceEndPointRepository.save(serviceEndpoint);
    }
    
    public void deleteByNamespaceEntity(NamespaceEntity namespace) {
    	List<ServiceEntity> list = serviceRepository.findByNamespace(namespace);
    	list.forEach(s ->  delete(s));
    }

	@Override
	public boolean isDuplicateName(Long clusterIdx, String namespace, String name) {
		Object obj = serviceRepository.getService(clusterIdx, namespace, name);
		return obj != null;
	}
}
