package kr.co.strato.domain.pod.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.node.repository.NodeRepository;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.portal.workload.model.PodDto;

@Service
public class PodDomainService {
    @Autowired
    private PodRepository podRepository;
    
    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;
    
    @Autowired
    private NodeRepository nodeRepository;


    public PodEntity get(Long podId) {
    	PodEntity pod = podRepository.findById(podId).orElseThrow(() -> new NotFoundResourceException("pod id:"+ podId));
    	return pod;
    }
    
    public Page<PodEntity> getPods(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId) {
        return podRepository.getPodList(pageable, projectId, clusterId, namespaceId, nodeId);
    }
    
    public StatefulSetEntity getPodStatefulSet(Long podId) {
    	return podRepository.getPodStatefulSet(podId);
    }
    
    /**
     * 
     * @param pod
     * @param clusterId
     * @param namespaceName
     * @param kind : resourceType
     * @return
     */
    public Long register(PodEntity pod, Long clusterId, String namespaceName, String kind) {
    	addNamespace(pod, clusterId, namespaceName);
//        addNode(pod, clusterId, namespaceName);
//    	if (kind.toLowerCase().equals(ResourceType.statefulSet)) {
//    		addStatefulSet(pod, clusterId, namespaceName);
//    	}
//    	addStatefulSet(pod, clusterId, namespaceName);
        podRepository.save(pod);
        return pod.getId();
    }
    
    private void addNamespace(PodEntity pod, Long clusterId, String namespaceName){
        Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());

        if(optCluster.isPresent()){
            ClusterEntity cluster = optCluster.get();
            List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);

            if(namespaces != null && namespaces.size() > 0){
            	pod.setNamespace(namespaces.get(0));
            }
        }
    }
    
    private void addNode(PodEntity pod, Long clusterId, String nodeName){
        Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());

        if(optCluster.isPresent()){
            ClusterEntity cluster = optCluster.get();
            // node 찾는 Repository 필요
//            List<NamespaceEntity> namespaces = nodeRepository.findByName(nodeName);
//
//            if(namespaces != null && namespaces.size() > 0){
//            	pod.setNamespace(namespaces.get(0));
//            }
        }
    }
    
//    private void addStatefulSet(PodEntity pod, Long clusterId, String nodeName){
//        Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());
//
//        if(optCluster.isPresent()){
//            ClusterEntity cluster = optCluster.get();
//            // node 찾는 Repository 필요
////            List<NamespaceEntity> namespaces = nodeRepository.findByName(nodeName);
////
////            if(namespaces != null && namespaces.size() > 0){
////            	pod.setNamespace(namespaces.get(0));
////            }
//        }
//    }
}
