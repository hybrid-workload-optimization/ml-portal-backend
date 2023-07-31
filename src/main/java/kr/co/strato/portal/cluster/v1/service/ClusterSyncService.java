package kr.co.strato.portal.cluster.v1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import kr.co.strato.adapter.k8s.namespace.service.NamespaceAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.persistentVolume.service.PersistentVolumeAdapterService;
import kr.co.strato.adapter.k8s.storageClass.service.StorageClassAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.pod.service.PodDomainService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterSyncService {

	@Autowired
	NamespaceAdapterService namespaceAdapterService;
	
	@Autowired
	NodeAdapterService nodeAdapterService;
	
	@Autowired
	PersistentVolumeAdapterService persistentVolumeAdapterService;
	
	@Autowired
	StorageClassAdapterService storageClassAdapterService;
	
	@Autowired
	ClusterNamespaceService clusterNamespaceService;
	
	@Autowired
	ClusterNodeService clusterNodeService;
	
	@Autowired
	ClusterPersistentVolumeService clusterPersistentVolumeService;
	
	@Autowired
	ClusterStorageClassService clusterStorageClassService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	NodeDomainService nodeDomainService;
	
	@Autowired
	PodDomainService podDomainService;
	
	
	/**
	 * Cluster 관련 정보 동기화(신규 등록)
	 * (namespace, node, pv, storage class)
	 * 
	 * @param clusterId
	 * @param clusterIdx
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void syncCluster(Long clusterId, Long clusterIdx) throws Exception {
		// k8s - get namespace
		List<Namespace> namespaces = null;
		try {
			namespaces = namespaceAdapterService.getNamespaceList(clusterId);
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		// k8s - get node
		List<Node> nodes = null;
		try {
			nodes = nodeAdapterService.getNodeList(clusterId);
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		// k8s - get pv
		List<PersistentVolume> persistentVolumes = null;
		try {
			persistentVolumes = persistentVolumeAdapterService.getPersistentVolumeList(clusterId);
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		// k8s - get storage class
		List<StorageClass> storageClasses = null;
		try {
			storageClasses = storageClassAdapterService.getStorageClassList(clusterId);
		} catch (Exception e) {
			log.error("", e);
		}
		
		
		// db - insert namespace
		if(namespaces != null) {
			clusterNamespaceService.synClusterNamespaceSave(namespaces, clusterIdx);
		}
		
				
		// db - insert node
		if(nodes != null) {
			clusterNodeService.synClusterNodeSave(nodes, clusterIdx);
		}
		
		
		// db - insert storage class
		if(storageClasses != null) {
			clusterStorageClassService.synClusterStorageClassSave(storageClasses, clusterIdx);
		}
		
		
		// db - insert pv
		if(persistentVolumes != null) {
			clusterPersistentVolumeService.synClusterPersistentVolumeSave(persistentVolumes, clusterIdx);
		}
		
		
	}
	
	/**
	 * Cluster 노드 정보 동기화(업데이트)
	 * 
	 * @param clusterIdx
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void syncClusterNode(Long clusterIdx) throws Exception {
		// db - get cluster
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		// k8s - get node
		List<Node> nodes = nodeAdapterService.getNodeList(clusterEntity.getClusterId());
		
		// compare node information
		ArrayList<NodeEntity> nodeEntityList = new ArrayList<>();
		nodeEntityList.addAll(clusterEntity.getNodes());
		
		// base on k8s node
		for (Node nodeK8s : nodes) {
			String uid = nodeK8s.getMetadata().getUid();
			
			if (uid == null) continue;
			
			boolean isExist = false;
			for (int i = 0; i < nodeEntityList.size(); i++) {
				if (nodeEntityList.get(i).getUid().equals(uid)) {
					isExist = true;
					
					// db - update node
					NodeEntity nodeEntity = clusterNodeService.toEntity(nodeK8s, clusterEntity.getClusterIdx());
					nodeEntity.setId(nodeEntityList.get(i).getId());
					
					log.debug("[syncClusterNode] updated node : {}", nodeEntity.toString());
					nodeDomainService.register(nodeEntity);
					
					// remove(self)
					log.debug("[syncClusterNode] removed node : {}", nodeEntityList.get(i).toString());
					nodeEntityList.remove(i);
				}
			}
			
			if (!isExist) {
				// db - insert node
				NodeEntity nodeEntity = clusterNodeService.toEntity(nodeK8s, clusterEntity.getClusterIdx());
				
				log.debug("[syncClusterNode] inserted node : {}", nodeEntity.toString());
				nodeDomainService.register(nodeEntity);
			}
		}
		
		// db - delete node
		for (NodeEntity nodeEntity : nodeEntityList) {
			podDomainService.deleteByNode(nodeEntity);
			nodeDomainService.delete(nodeEntity.getId());
		}
	}
}
