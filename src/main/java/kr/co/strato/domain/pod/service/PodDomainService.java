package kr.co.strato.domain.pod.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Volume;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.repository.JobRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.repository.NodeRepository;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.repository.PersistentVolumeClaimRepository;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.PodJobEntity;
import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodReplicaSetEntity;
import kr.co.strato.domain.pod.model.PodStatefulSetEntity;
import kr.co.strato.domain.pod.repository.PodJobRepository;
import kr.co.strato.domain.pod.repository.PodPersistentVolumeClaimRepository;
import kr.co.strato.domain.pod.repository.PodReplicaSetRepository;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.pod.repository.PodStatefulSetRepository;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.repository.ReplicaSetRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.repository.StatefulSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;
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
    
    @Autowired
    private PodReplicaSetRepository podReplicaSetRepository;
    
    @Autowired
    private PersistentVolumeClaimRepository pvcRepository;
    
    @Autowired
    private PodJobRepository podJobRepository;

    public PodEntity get(Long podId) {
    	Optional<PodEntity> pod = podRepository.findById(podId.longValue());
    	if (pod.isPresent()) {
    		return pod.get();
    	} else {
    		throw new NotFoundResourceException("pod_idx : " + podId);
    	}
    }
    
    public ClusterEntity getClusterEntity(Long podId){
    	PodEntity entity = get(podId);

        return entity.getNamespace().getCluster();
    }
    
    public Long update(Long podId, PodEntity updateEntity) {
    	PodEntity oldEntity = get(podId);
        changeToNewData(oldEntity, updateEntity);
        podRepository.save(oldEntity);
        return oldEntity.getId();
    }
    
    public Page<PodEntity> getPods(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId) {
        return podRepository.getPodList(pageable, projectId, clusterId, namespaceId, nodeId);
    }
    
    public void delete(PodEntity pod) {
		podRepository.delete(pod);
    }
    
    public void deleteByClusterIdx(Long clusterIdx) {
    	Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterIdx.longValue());
    	
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
    
    public StatefulSetEntity getPodStatefulSet(Long podId) {
    	return podRepository.getPodStatefulSet(podId);
    }
    
//    public DaemonSetEntity getPodDaemonSet(Long podId) {
//    	return podRepository.getPodDaemonSet(podId);
//    }
    
    public ReplicaSetEntity getPodReplicaSet(Long podId) {
    	return podRepository.getPodReplicaSet(podId);
    }
    
    public JobEntity getPodJob(Long podId) {
    	return podRepository.getPodJob(podId);
    }
    
    
    /**
     * 
     * @param pod
     * @param clusterId
     * @param namespaceName
     * @param kind : resourceType
     * @return
     */
    public Long register(PodEntity pod, ClusterEntity cluster, String namespaceName, String kind) {
        List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);
        
        try {
        	// namepsace와 node가 db에 없으면 저장 x
        	if(namespaces != null && namespaces.size() > 0){
            	pod.setNamespace(namespaces.get(0));
            	
            	if (pod.getNode() != null) {
            		String nodeName = pod.getNode().getName();
            		List<NodeEntity> node = nodeRepository.findByNameAndClusterIdx(nodeName, cluster);
            		if (node != null && node.size() > 0 ) {
                		pod.setNode(node.get(0));
                		
                		podRepository.save(pod);
                		
                		// TODO pvcEntity save
                		addPersistentVolumeClaim(pod);
                	}
            	}
            	
            	if (pod != null && !kind.isEmpty()) {
                	// 첫글자 소문자
                    kind = kind.substring(0, 1).toLowerCase() + kind.substring(1);
                    
                	if (kind.equals(ResourceType.statefulSet.get())) {
                		addStatefulSet(pod);
                	} else if (kind.equals(ResourceType.replicaSet.get())) {
                		addReplicaSet(pod);
                	} else if (kind.equals(ResourceType.daemonSet.get())) {
//                    		addDeamonSet(pod);
                	} else if (kind.equals(ResourceType.job.get())) {
                		addJob(pod);
                	}
                }
            }
        } catch (Exception e) {
			// TODO: handle exception
        	log.error(e.getMessage(), e);
		}
            
        return pod.getId();
    }
    
    
    private void addStatefulSet(PodEntity pod){

        String ownerUid = pod.getOwnerUid();
        NamespaceEntity namespace = pod.getNamespace();
        
        StatefulSetEntity statefulSet = statefulSetRepository.findByUidAndNamespaceIdx(ownerUid, namespace);
        PodStatefulSetEntity podStatefulSet = new PodStatefulSetEntity();

        if(statefulSet != null){
        	podStatefulSet.setPod(pod);
        	podStatefulSet.setStatefulSet(statefulSet);
        	
        	podStatefulSetRepository.save(podStatefulSet);
        }
    }
    private void addReplicaSet(PodEntity pod){
    	String ownerUid = pod.getOwnerUid();
        NamespaceEntity namespace = pod.getNamespace();
        
        ReplicaSetEntity replicaSet = replicaSetRepository.findByUidAndNamespaceIdx(ownerUid, namespace);
        PodReplicaSetEntity podReplicaSet = new PodReplicaSetEntity();

        if(replicaSet != null){
        	podReplicaSet.setPod(pod);
        	podReplicaSet.setReplicaSet(replicaSet);
        	
        	podReplicaSetRepository.save(podReplicaSet);
        }
    }

    private void addDeamonSet(PodEntity pod){

    	String ownerUid = pod.getOwnerUid();
        NamespaceEntity namespace = pod.getNamespace();
        // TODO Daemon Set
        
//        DaemonSetEntity daemonSet = daemonSetRepository.findByUidAndNamespaceIdx(ownerUid, namespace);
//        PodDaemonSetEntity podDaemonSet = new PodDaemonSetEntity();
//
//        if(daemonSet != null){
//        	podDaemonSet.setPod(pod);
//        	podDaemonSet.setDaemonSet(daemonSet);
//        	
//        	podDaemonSetRepository.save(podDaemonSet);
//        }
    }
    private void addJob(PodEntity pod){

    	String ownerUid = pod.getOwnerUid();
        NamespaceEntity namespace = pod.getNamespace();
        
        JobEntity job = jobRepository.findByUidAndNamespaceIdx(ownerUid, namespace);
        PodJobEntity podJob = new PodJobEntity();

        if(job != null){
        	podJob.setPod(pod);
        	podJob.setJob(job);
        	
        	podJobRepository.save(podJob);
        }
    }
    
    private void addPersistentVolumeClaim(PodEntity pod){
    	List<Volume> volumes = pod.getVolumes();
    	if (!volumes.isEmpty()) {
    		for(Volume volume : volumes) {
    			PersistentVolumeClaimVolumeSource pvc = volume.getPersistentVolumeClaim();
    			if (pvc != null) {
    				String pvcName = pvc.getClaimName();
                	PersistentVolumeClaimEntity pvcEntity = pvcRepository.findByNameAndNamespaceId(pvcName, pod.getNamespace().getId());
                	if (pvcEntity != null) {
                		PodPersistentVolumeClaimEntity podPersistentVolumeClaim = new PodPersistentVolumeClaimEntity();
                		podPersistentVolumeClaim.setPod(pod);
                		podPersistentVolumeClaim.setPersistentVolumeClaim(pvcEntity);
                		
                		podPersistentVolumeClaimRepository.save(podPersistentVolumeClaim);
                	}
    			}
    		}
    	}
    }
    
    private void changeToNewData(PodEntity oldEntity, PodEntity newEntity){
        oldEntity.setOwnerUid(newEntity.getOwnerUid());
        oldEntity.setCreatedAt(newEntity.getCreatedAt());
        oldEntity.setCpu(newEntity.getCpu());
        oldEntity.setMemory(newEntity.getMemory());
        oldEntity.setLabel(newEntity.getLabel());
        oldEntity.setAnnotation(newEntity.getAnnotation());
        oldEntity.setLabel(newEntity.getLabel());
    }
    
    /**
     * Node 정보를 이용하여 Pod 삭제
     * 
     * @param nodeEntity
     */
    public void deleteByNode(NodeEntity nodeEntity) {
    	List<PodEntity> pods = podRepository.findByNode(nodeEntity);
		pods.forEach(pod -> podRepository.delete(pod));
    }
}
