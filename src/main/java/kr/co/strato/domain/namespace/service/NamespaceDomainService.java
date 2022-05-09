package kr.co.strato.domain.namespace.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.common.service.NonNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class NamespaceDomainService extends NonNamespaceDomainService {
	
	@Autowired
	private NamespaceRepository namespaceRepository;
	
	public Long register(NamespaceEntity namespaceEntity) {
		namespaceRepository.save(namespaceEntity);
		return namespaceEntity.getId();
	}
	public Page<NamespaceEntity> getList(Pageable pageable) {
		return namespaceRepository.findAll(pageable);
	}
	public Page<NamespaceEntity> getNamespaceList(Pageable pageable, Long clusterId,String name) {
		return namespaceRepository.getNamespaceList(pageable,  clusterId,name);
	}
	
	public boolean delete(Long id) {
		namespaceRepository.deleteNamespace(id);
		return true;
	}
	
	public NamespaceEntity getDetail(Long id) {
		Optional<NamespaceEntity> namespace = namespaceRepository.findById(id);
		if (namespace.isPresent()) {
			return namespace.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}
	
	public List<NamespaceEntity> findByNameAndClusterIdx(String name,ClusterEntity clusterEntity) {
		return namespaceRepository.findByNameAndClusterIdx(name,clusterEntity);
	}
	
    public ClusterEntity getCluster(Long id){
    	NamespaceEntity entity = getDetail(id);
        ClusterEntity cluster =  entity.getCluster();
        return cluster;
    }
    
    public Long update(NamespaceEntity updateEntity,Long namespaceId) {
    	NamespaceEntity oldEntity = get(namespaceId);
        changeToNewData(oldEntity, updateEntity);
    	namespaceRepository.save(oldEntity);
		return oldEntity.getId();
	}

	public List<NamespaceEntity> findByClusterIdx(Long clusterIdx){
		ClusterEntity cluster = ClusterEntity.builder().clusterIdx(clusterIdx).build();
		return namespaceRepository.findByClusterIdx(cluster);
	}
	
	public NamespaceEntity get(Long namespaceId) {
		NamespaceEntity namespaceEntity = namespaceRepository.findById(namespaceId)
				.orElseThrow(() -> new NotFoundResourceException("namespace id:" + namespaceId));

		return namespaceEntity;
	}

	private void changeToNewData(NamespaceEntity oldEntity, NamespaceEntity newEntity) {
		oldEntity.setUid(newEntity.getUid());
		oldEntity.setCreatedAt(newEntity.getCreatedAt());
		oldEntity.setStatus(newEntity.getStatus());
		oldEntity.setAnnotation(newEntity.getAnnotation());
		oldEntity.setLabel(newEntity.getLabel());
	}
	
	public void deleteByClusterIdx(Long clusterIdx) {
		List<NamespaceEntity> list = findByClusterIdx(clusterIdx);
		list.forEach(n -> delete(n.getId()));
	}
	
	
	@Override
	public boolean isDuplicateName(Long clusterIdx, String name) {
		ClusterEntity cluster = ClusterEntity.builder().clusterIdx(clusterIdx).build();
		List<NamespaceEntity> list = findByNameAndClusterIdx(name, cluster);
		return list != null && list.size() > 0;
	}
	
}
