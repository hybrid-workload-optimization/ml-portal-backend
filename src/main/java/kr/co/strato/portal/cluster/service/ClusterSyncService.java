package kr.co.strato.portal.cluster.service;

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
	
	
	@Transactional(rollbackFor = Exception.class)
	public void syncCluster(Long clusterId, Long clusterIdx) throws Exception {
		// k8s - get namespace
		List<Namespace> namespaces = namespaceAdapterService.getNamespaceList(clusterId);
		
		// k8s - get node
		List<Node> nodes = nodeAdapterService.getNodeList(clusterId);
		
		// k8s - get pv
		List<PersistentVolume> persistentVolumes = persistentVolumeAdapterService.getPersistentVolumeList(clusterId);
		
		// k8s - get storage class
		List<StorageClass> storageClasses = storageClassAdapterService.getStorageClassList(clusterId);
		
		// db - insert namespace
		clusterNamespaceService.synClusterNamespaceSave(namespaces, clusterIdx);
				
		// db - insert node
		clusterNodeService.synClusterNodeSave(nodes, clusterIdx);
		
		// db - insert pv
		clusterPersistentVolumeService.synClusterPersistentVolumeSave(persistentVolumes, clusterIdx);
		
		// db - storage class
		clusterStorageClassService.synClusterStorageClassSave(storageClasses, clusterIdx);
	}
}
