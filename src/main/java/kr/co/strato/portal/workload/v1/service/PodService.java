package kr.co.strato.portal.workload.v1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.config.v1.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.v1.model.PersistentVolumeClaimDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.v1.model.PodDto;
import kr.co.strato.portal.workload.v1.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PodService extends InNamespaceService {
	@Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private PodDomainService podDomainService;
    
    @Autowired
    private PodAdapterService podAdapterService;
    
    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;
    
    @Autowired
    private PersistentVolumeClaimDomainService persistentVolumeClaimDomainService;
    
    @Autowired
    private ProjectDomainService projectDomainService;
    
    @Autowired
	private ProjectAuthorityService projectAuthorityService;
    
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createPod(PodDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity cluster = clusterDomainService.get(clusterIdx);
        
        //이름 중복 채크
        duplicateCheckResourceCreation(clusterIdx, reqCreateDto.getYaml());

        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<Pod> pods = podAdapterService.create(cluster.getClusterId(), yaml);
        
        // k8s data insert
        List<Long> ids = pods.stream().map( s -> {
        	try {
                String namespaceName = s.getMetadata().getNamespace();
                // ownerReferences 추가
                PodEntity pod = PodMapper.INSTANCE.toEntity(s);
                
                // node name 없어서 재조회해서 update
                Pod k8sPodDetail = podAdapterService.get(cluster.getClusterId(), namespaceName, pod.getPodName());
                
                PodEntity podDetail = PodMapper.INSTANCE.toEntity(k8sPodDetail);
                podDetail.setYaml(yaml);
                Long id = podDomainService.register(podDetail, cluster, namespaceName, null);
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
                pod.setYaml(yaml);
				Long id = podDomainService.update(podId, pod);
                return id;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("pod register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }
    
    /*
    @Transactional(rollbackFor = Exception.class)
    public Page<PodDto.ResListDto> getPods(Pageable pageable, PodDto.SearchParam searchParam) {
    	Long clusterIdx = searchParam.getClusterIdx();
    	Long namespaceIdx = searchParam.getNamespaceId();
    	ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
    	
    	String namespace = null;
    	if(namespaceIdx != null) {
    		NamespaceEntity namespaceEntity = namespaceDomainService.get(namespaceIdx);
    		namespace = namespaceEntity.getName();
    	}
    	
    	
    	Long clusterId = clusterEntity.getClusterId();
 		List<Pod> k8sPods = podAdapterService.getList(clusterId, null, null, namespace, null);
 		
 		List<PodEntity> list = new ArrayList<>();
 		for(Pod p : k8sPods) {
 			try {
				PodEntity pod = PodMapper.INSTANCE.toEntity(p);
				list.add(pod);
			} catch (Exception e) {
                log.error(e.getMessage(), e);
            }
 		}
 		List<PodDto.ResListDto> dtos = list.stream().map(e -> PodDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
 		dtos.forEach(p -> p.setClusterName(clusterEntity.getClusterName()));
 		
        Page<PodDto.ResListDto> pages = new PageImpl<>(dtos, pageable, list.size());
        return pages;
    }
    */
    
    
    @Transactional(rollbackFor = Exception.class)
    public Page<PodDto.ResListDto> getPods(Pageable pageable, PodDto.SearchParam searchParam) {
    	Long clusterIdx = searchParam.getClusterIdx();
    	Long namespaceIdx = searchParam.getNamespaceId();
    	Integer page = pageable.getPageNumber();

    	if (page == 0
//    			&& projectId == null 
//    			&& clusterIdx == null 
    			&& clusterIdx != null 
//    			&& searchParam.getNamespaceId() == null 
    			&& searchParam.getNodeId() == null) {
//    		List<ClusterEntity> clusters = clusterDomainService.getListAll();
//    		for(ClusterEntity clusterEntity : clusters) {
    			
    			
    		
    			ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
                Long clusterId = clusterEntity.getClusterId();
        		List<Pod> k8sPods = podAdapterService.getList(clusterId, null, null, null, null);
        		
                // cluster id 기준 db row delete (관련된 모든 mapping table의 값도 삭제)
        		podDomainService.deleteByClusterIdx(clusterEntity);
        		
                // k8s data insert
        		List<Long> ids = k8sPods.stream().map( s -> {
        			try {
        				PodEntity pod = PodMapper.INSTANCE.toEntity(s);
        				pod.setCluster(clusterEntity);
        				String namespaceName = pod.getNamespace().getName();
        				String kind = pod.getKind();
        				Long id = null;
        				//if (pod.getNode() != null) {
        				
    					id = podDomainService.register(pod, clusterEntity, namespaceName, kind);
        				//}
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
    
    

    public PodDto.ResDetailDto getPodDetail(Long podId, UserDto loginUser){
    	// get pod entity
    	PodEntity entity = podDomainService.get(podId);
    	
    	
    	Long clusterIdx = entity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
    	
    	
    	PodDto.ResDetailDto dto = PodDtoMapper.INSTANCE.toResDetailDto(entity);
    	dto.setProjectIdx(projectIdx);
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
		Map<String, String> selector = searchParam.getSelector();
//    	List<Pod> k8sPods = podAdapterService.getList(clusterId, nodeName, ownerUid, namespace, null);
    	List<Pod> k8sPods = podAdapterService.getList(clusterId, nodeName, ownerUid, namespace, selector);
    	List<PodDto.ResListDto> dtoList = new ArrayList<>();
    	
    	for(Pod k8sPod : k8sPods) {
    		PodEntity podEntity = PodMapper.INSTANCE.toEntity(k8sPod);
    		
    		PodDto.ResListDto dto = PodDtoMapper.INSTANCE.toResK8sListDto(podEntity);
    		dtoList.add(dto);
    	}
    	
    	return dtoList;
    }
    
    public List<PersistentVolumeClaimDto.ResListDto> getPodPersistentVolumeClaim(Long podId) {
    	List<PersistentVolumeClaimEntity> result = persistentVolumeClaimDomainService.getPodPersistentVolumeClaimList(podId);
    	List<PersistentVolumeClaimDto.ResListDto> dtos = result.stream().map(e -> PersistentVolumeClaimDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
    	return dtos;
    }
    
    public Boolean deletePod(Long podId) {
    	Boolean result = false;
    	try {
    		PodEntity entity = podDomainService.get(podId);
        	podDomainService.delete(entity);
        	podAdapterService.delete(entity.getNamespace().getCluster().getClusterId(), entity.getNamespace().getName(), entity.getPodName());
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
        String yaml = entity.getYaml();
        if(yaml == null) {
        	 //get k8s statefulSet model
            String podName = entity.getPodName();
            String namespaceName = entity.getNamespace().getName();
            Long clusterId = entity.getNamespace().getCluster().getClusterId();

            yaml = podAdapterService.getYaml(clusterId, namespaceName, podName);
        }
        yaml = Base64Util.encode(yaml);
        return yaml;
    }

	@Override
	protected InNamespaceDomainService getDomainService() {
		return podDomainService;
	}
}
