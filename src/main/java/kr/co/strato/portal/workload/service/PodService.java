package kr.co.strato.portal.workload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.pod.model.PodMapper;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PodService {
	@Autowired
    private ClusterDomainService clusterDomainService;
    @Autowired
    private PodDomainService podDomainService;
    
    @Autowired
    private PodAdapterService podAdapterService;
    
    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;
    
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createPod(PodDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity cluster = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<Pod> pods = podAdapterService.create(cluster.getClusterId(), yaml);
        
        // k8s data insert
        List<Long> ids = pods.stream().map( s -> {
        	try {
                String namespaceName = s.getMetadata().getNamespace();
                // ownerReferences 추가
                PodEntity pod = PodMapper.INSTANCE.toEntity(s);
                // TODO pvc
            //  PersistentVolumeClaimEntity pvcEntity = PersistentVolumeMapper.INSTANCE.toEntity(s);
				PersistentVolumeClaimEntity pvcEntity = null;
                
                // node name 없어서 재조회해서 update
                Pod k8sPodDetail = podAdapterService.get(cluster.getClusterId(), namespaceName, pod.getPodName());
                
                PodEntity podDetail = PodMapper.INSTANCE.toEntity(k8sPodDetail);
                Long id = podDomainService.register(podDetail, cluster, namespaceName, null, pvcEntity);
                podDomainService.update(id, podDetail);
                
                return id;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("pod register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public List<Long> updatePod(Long podId, PodDto.ReqUpdateDto reqUpdateDto){
        String yaml = Base64Util.decode(reqUpdateDto.getYaml());
        ClusterEntity cluster = podDomainService.getClusterEntity(podId);
        Long clusterId = cluster.getClusterId();

        List<Pod> pods = podAdapterService.update(clusterId, yaml);
        
        // k8s data insert
        List<Long> ids = pods.stream().map( s -> {
        	try {
                String namespaceName = s.getMetadata().getNamespace();
                // ownerReferences 추가
                PodEntity pod = PodMapper.INSTANCE.toEntity(s);

				Long id = podDomainService.update(podId, pod);
                return id;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("pod register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Page<PodDto.ResListDto> getPods(Pageable pageable, PodDto.SearchParam searchParam) {
    	// TODO 수정: clusterId db에서 전체 가져와서 for문 돌리기
    	Long projectId = searchParam.getProjectId();
    	Long clusterIdx = searchParam.getClusterIdx();
    	Integer page = pageable.getPageNumber();

    	if (page == 0
//    			&& projectId == null 
//    			&& clusterIdx == null 
    			&& clusterIdx != null 
    			&& searchParam.getNamespaceId() == null 
    			&& searchParam.getNodeId() == null) {
//    		List<ClusterEntity> clusters = clusterDomainService.getListAll();
//    		for(ClusterEntity clusterEntity : clusters) {
    			ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
                Long clusterId = clusterEntity.getClusterId();
        		List<Pod> k8sPods = podAdapterService.getList(clusterId, null, null, null, null);
        		
                // cluster id 기준 db row delete (관련된 모든 mapping table의 값도 삭제)
        		// TODO idx로 해야되나???
        		podDomainService.deleteByClusterIdx(clusterIdx);
        		
                // k8s data insert
        		List<Long> ids = k8sPods.stream().map( s -> {
        			try {
        				PodEntity pod = PodMapper.INSTANCE.toEntity(s);
                        // TODO pvc
        				//  PersistentVolumeClaimEntity pvcEntity = PersistentVolumeMapper.INSTANCE.toEntity(s);
        				PersistentVolumeClaimEntity pvcEntity = null;
        				String namespaceName = pod.getNamespace().getName();
        				String kind = pod.getKind();
        				
        				Long id = podDomainService.register(pod, clusterEntity, namespaceName, kind, pvcEntity);
        				return id;
        			} catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new InternalServerException("pod register error");
                    }
        		}).collect(Collectors.toList());
//    		}
    	}
    	
    	Page<PodEntity> pods = podDomainService.getPods(pageable, searchParam.getProjectId(), searchParam.getClusterIdx(), searchParam.getNamespaceId(), searchParam.getNodeId());
        List<PodDto.ResListDto> dtos = pods.stream().map(e -> PodDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
        Page<PodDto.ResListDto> pages = new PageImpl<>(dtos, pageable, pods.getTotalElements());
        
    	return pages;
    }
    

    public PodDto.ResDetailDto getPodDetail(Long podId){
    	// get pod entity
    	PodEntity entity = podDomainService.get(podId);
    	
    	PodDto.ResDetailDto dto = PodDtoMapper.INSTANCE.toResDetailDto(entity);
    	return dto;
    }
    
    public PodDto.ResOwnerDto getPodOwnerInfo(Long podId, String resourceType) {
    	PodDto.ResOwnerDto dto = new PodDto.ResOwnerDto();
    	if (resourceType.equals(ResourceType.statefulSet.get())) {
    		StatefulSetEntity entity = podDomainService.getPodStatefulSet(podId);
    		
    		if (entity != null) {
	    		Long clusterId = entity.getNamespace().getCluster().getClusterId();
	    		String namespace = entity.getNamespace().getName();
	    		
	    		String statefulSetName = entity.getPodStatefulSets().get(0).getStatefulSet().getStatefulSetName();
	    		StatefulSet k8sStatefulSet = statefulSetAdapterService.get(clusterId, namespace, statefulSetName);
	    		
	    		dto = PodDtoMapper.INSTANCE.toResStatefulSetOwnerInfoDto(entity, k8sStatefulSet, resourceType);
    		}
    	} else if (resourceType.equals(ResourceType.daemonSet.get())) {
//    		DaemonSetEntity entity = podDomainService.getPodDaemonSet(podId);
//    		
//    		Long clusterId = entity.getNamespace().getCluster().getClusterId();
//    		String namespace = entity.getNamespace().getName();
//    		
//    		String DaemonSetName = entity.getPodDaemonSets().get(0).getDaemonSet().getDaemonSetName();
//    		StatefulSet k8sStatefulSet = daemonSetAdapterService.get(clusterId, namespace, statefulSetName);
//    		
//    		dto = PodDtoMapper.INSTANCE.toResDaemonSetOwnerInfoDto(entity, k8sStatefulSet, resourceType);
    		
    	} else if (resourceType.equals(ResourceType.replicaSet.get())) {
    		ReplicaSetEntity entity = podDomainService.getPodReplicaSet(podId);
    		
    		if (entity != null) {
	    		Long clusterId = entity.getNamespace().getCluster().getClusterId();
	    		String namespace = entity.getNamespace().getName();
	    		
	    		String replicaSetName = entity.getPodReplicaSets().get(0).getReplicaSet().getReplicaSetName();
	    		StatefulSet k8sStatefulSet = statefulSetAdapterService.get(clusterId, namespace, replicaSetName);
	    		
	    		dto = PodDtoMapper.INSTANCE.toResReplicaSetOwnerInfoDto(entity, k8sStatefulSet, resourceType);
    		}
    		
    	} else if (resourceType.equals(ResourceType.job.get())) {
    		JobEntity entity = podDomainService.getPodJob(podId);
    		
    		if (entity != null) {
    			Long clusterId = entity.getNamespaceEntity().getCluster().getClusterId();
        		String namespace = entity.getNamespaceEntity().getName();
        		
        		String statefulSetName = entity.getPodJobs().get(0).getJob().getJobName();
        		StatefulSet k8sStatefulSet = statefulSetAdapterService.get(clusterId, namespace, statefulSetName);
        		
        		dto = PodDtoMapper.INSTANCE.toResJobOwnerInfoDto(entity, k8sStatefulSet, resourceType);
    		}
    	}
    	
    	return dto;
    }
    
    public ResponseEntity<ByteArrayResource> getLogDownloadFile(Long clusterId, String namespaceName, String podName) {
    	ResponseEntity<ByteArrayResource> result = podAdapterService.getLogDownloadFile(clusterId, namespaceName, podName);
    	return result;
    }
    
    public List<PodDto.ResListDto> getPodOwnerPodList(Long clusterId, PodDto.OwnerSearchParam searchParam) {
		String nodeName = searchParam.getNodeName();
		String ownerUid = searchParam.getOwnerUid();
		String namespace = searchParam.getNamespaceName();
//		Map<String, String> selector = searchParam.getSelector();
    	List<Pod> k8sPods = podAdapterService.getList(clusterId, nodeName, ownerUid, namespace, null);
//    	List<Pod> k8sPods = podAdapterService.getList(clusterId, nodeName, ownerUid, namespace, selector);
    	List<PodDto.ResListDto> dtoList = new ArrayList<>();
    	
    	for(Pod k8sPod : k8sPods) {
    		PodEntity podEntity = PodMapper.INSTANCE.toEntity(k8sPod);
    		
    		PodDto.ResListDto dto = PodDtoMapper.INSTANCE.toResK8sListDto(podEntity);
    		dtoList.add(dto);
    	}
    	
    	return dtoList;
    }
    
    public Boolean deletePod(Long podId) {
    	Boolean result = false;
    	try {
    		PodEntity entity = podDomainService.get(podId);
        	podDomainService.delete(entity);
        	result = true;
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    		throw new InternalServerException("pod deleted fail");
    	}
    	return result;
    }
    
    public String getPodtYaml(Long podId) {
    	//get statefulSet entity
        PodEntity entity = podDomainService.get(podId);

        //get k8s statefulSet model
        String podName = entity.getPodName();
        String namespaceName = entity.getNamespace().getName();
        Long clusterId = entity.getNamespace().getCluster().getClusterId();

        String yaml = podAdapterService.getYaml(clusterId, namespaceName, podName);
        yaml = Base64Util.encode(yaml);

        return yaml;
    }
}
