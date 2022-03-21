package kr.co.strato.domain.pod.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.repository.NodeRepository;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.PodStatefulSetEntity;
import kr.co.strato.domain.pod.repository.PodPersistentVolumeClaimRepository;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.pod.repository.PodStatefulSetRepository;
import kr.co.strato.domain.replicaset.repository.ReplicaSetRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.repository.StatefulSetRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PodDomainService {
    @Autowired
    private PodRepository podRepository;
    
    @Autowired
    private PodPersistentVolumeClaimRepository podPersistentVolumeClaimRepository;
    
    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;
    
    @Autowired
    private NodeRepository nodeRepository;
    
    @Autowired
    private StatefulSetRepository statefulSetRepository;
    
    @Autowired
    private ReplicaSetRepository replicaSetRepository;
    
//    @Autowired
//    private DaemonSetRepository daemonSetRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PodStatefulSetRepository podStatefulSetRepository;
    


    public PodEntity get(Long podId) {
    	PodEntity pod = PodEntity.builder().id(podId).build();
//    	List<PodPersistentVolumeClaimEntity> result = podPersistentVolumeClaimRepository.findByPod(pod);
    	return pod;
    }
    
    public Page<PodEntity> getPods(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId) {
        return podRepository.getPodList(pageable, projectId, clusterId, namespaceId, nodeId);
    }
    
    public StatefulSetEntity getPodStatefulSet(Long podId) {
    	return podRepository.getPodStatefulSet(podId);
    }
    
    public void delete(Long clusterId) {
    	Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());
    	
    	if(optCluster.isPresent()){
    		ClusterEntity cluster = optCluster.get();
    		List<NamespaceEntity> namespaces = namespaceRepository.findByClusterIdx(cluster);
    		
    		// pod mapping table delete
    		for(NamespaceEntity namespace : namespaces) {
    			List<PodEntity> pods = podRepository.findAllByNamespaceIdx(namespace.getId());
    			
    			for(PodEntity pod : pods) {
    				podRepository.delete(pod);
    			}
    		}
    	}
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
    	Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());
    	String nodeName = pod.getNode().getName();

    	if(optCluster.isPresent()){
            ClusterEntity cluster = optCluster.get();
            List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);
            List<NodeEntity> node = nodeRepository.findByNameAndClusterIdx(nodeName, cluster);
            
            try {
            	// namepsace와 node가 db에 없으면 저장 x
            	if((namespaces != null && namespaces.size() > 0) && (node != null && node.size() > 0)){
                	pod.setNamespace(namespaces.get(0));
                	pod.setNode(node.get(0));
                	
                	if (!kind.isEmpty()) {
                    	// 첫글자 소문자
                        kind = kind.substring(0, 1).toLowerCase() + kind.substring(1);
                        
                    	if (kind.equals(ResourceType.statefulSet)) {
                    		addStatefulSet(pod);
                    	} else if (kind.equals(ResourceType.replicaSet)) {
                    		addReplicaSet(pod);
//                    	} else if (kind.toLowerCase().equals(ResourceType.daemonSet)) {
//                    		addDeamonSet(pod, clusterId, namespaceName);
                    	} else if (kind.equals(ResourceType.job)) {
                    		addJobSet(pod);
                    	}
                    }
                	podRepository.save(pod);
                	log.info("COMMIT::");
                }
            } catch (Exception e) {
				// TODO: handle exception
            	log.error(e.getMessage(), e);
			}
            
        }
        // TODO pod_persistent_volume_claim
        return pod.getId();
    }
    
    
    private void addStatefulSet(PodEntity pod){

        String statefulSetUid = pod.getOwnerUid();
        NamespaceEntity namespace = pod.getNamespace();
        // node 찾는 Repository 필요
        StatefulSetEntity statefulSet = statefulSetRepository.findByUidAndNamespaceIdx(statefulSetUid, namespace);
        PodStatefulSetEntity podStatefulSet = new PodStatefulSetEntity();

        if(statefulSet != null){
        	podStatefulSet.setPod(pod);
        	podStatefulSet.setStatefulSet(statefulSet);
        	
        	podStatefulSetRepository.save(podStatefulSet);
        }
    }
    private void addReplicaSet(PodEntity pod){

            // node 찾는 Repository 필요
//            List<NamespaceEntity> namespaces = nodeRepository.findByName(nodeName);
//
//            if(namespaces != null && namespaces.size() > 0){
//            	pod.setNamespace(namespaces.get(0));
//            }
    }
//    private void addDeamonSet(PodEntity pod, Long clusterId, String nodeName){
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
    private void addJobSet(PodEntity pod){

            // node 찾는 Repository 필요
//            List<NamespaceEntity> namespaces = nodeRepository.findByName(nodeName);
//
//            if(namespaces != null && namespaces.size() > 0){
//            	pod.setNamespace(namespaces.get(0));
//            }
    }
}
