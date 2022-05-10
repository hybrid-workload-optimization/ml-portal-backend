package kr.co.strato.portal.workload.service;

import static java.util.stream.Collectors.toList;

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

import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.adapter.k8s.pod.model.PodMapper;
import kr.co.strato.adapter.k8s.pod.service.PodAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.config.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.service.PersistentVolumeClaimService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.model.PodDto;
import kr.co.strato.portal.workload.model.PodDto.ApiOwnerSearchParam;
import kr.co.strato.portal.workload.model.PodDto.ApiReqUpdateDto;
import kr.co.strato.portal.workload.model.PodDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PodOnlyApiService extends InNamespaceService {
	
	@Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private PodDomainService podDomainService;
    
    @Autowired
    private PodAdapterService podAdapterService;
    
    @Autowired
    private ProjectDomainService projectDomainService;
    
    @Autowired
    private NamespaceDomainService namespaceDomainService;
    
    @Autowired
	private ProjectAuthorityService projectAuthorityService;
    
    @Autowired
    private PersistentVolumeClaimService pvcService;
    
    /**
     * 파드 생성
     * @param reqCreateDto
     * @return
     */
    public List<String> createPod(PodDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity cluster = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<Pod> pods = podAdapterService.create(cluster.getClusterId(), yaml);
        List<String> podNames = pods.stream().map(p -> p.getMetadata().getName()).collect(Collectors.toList());
        return podNames;
    }
    
    /**
     * 파드 업데이트
     * @param podId
     * @param reqUpdateDto
     * @return
     */
    public List<String> updatePod(ApiReqUpdateDto updateDto){
        String yaml = Base64Util.decode(updateDto.getYaml());        
        ClusterEntity cluster = podDomainService.getClusterEntity(updateDto.getClusterIdx());
        Long clusterId = cluster.getClusterId();

        List<Pod> pods = podAdapterService.update(clusterId, yaml);
        List<String> podNames = pods.stream().map(p -> p.getMetadata().getName()).collect(Collectors.toList());
        return podNames;
    }
    
    
    /**
     * 파드 리스트 반환.
     * @param pageable
     * @param searchParam
     * @return
     */
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
    
    
    
    /**
     * 파드 상세 정보 반환.
     * @param podId
     * @param loginUser
     * @return
     */
    public PodDto.ResDetailDto getPodDetail(Long clusterIdx, String namespace, String podName, UserDto loginUser) {
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
    	
		Pod pod = podAdapterService.get(cluster.getClusterId(), namespace, podName);
		PodEntity entity = PodMapper.INSTANCE.toEntity(pod);
		
    	PodDto.ResDetailDto dto = PodDtoMapper.INSTANCE.toResDetailDto(entity);
    	dto.setProjectIdx(projectIdx);
    	dto.setClusterId(cluster.getClusterId());
    	dto.setClusterName(cluster.getClusterName());
    	
    	
		OwnerReference owner = pod.getMetadata().getOwnerReferences().get(0);
		if(owner != null) {
			String ownerName = owner.getName();
			String ownerKind = owner.getKind();
			String ownerUid = owner.getUid();
			
			
			dto.setOwnerName(ownerName);
			dto.setOwnerKind(ownerKind);
			dto.setOwnerUid(ownerUid);
		}
		
		List<String> pvcNames = pod.getSpec().getVolumes().stream()
				.map(v -> v.getPersistentVolumeClaim())
				.filter(p -> p != null)
				.map(p -> p.getClaimName())
				.collect(toList());
		
		List<PersistentVolumeClaimDto.Detail> pvcList = new ArrayList<>();
		for(String pvcName : pvcNames) {
			try {
				PersistentVolumeClaimDto.Detail pvcDto = pvcService.getPersistentVolumeClaim(clusterIdx, namespace, pvcName);
				if (pvcDto != null) {
					pvcList.add(pvcDto);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		dto.setPvcList(pvcList);		
    	return dto;
    }
    
    /**
     * 파드 삭제.
     * @param clusterIdx
     * @param namespace
     * @param podName
     * @return
     */
    public Boolean deletePod(Long clusterIdx, String namespace, String podName) {
    	ClusterEntity cluster = clusterDomainService.get(clusterIdx);
    	return podAdapterService.delete(cluster.getClusterId(), namespace, podName);
    }
    
    /**
     * 로그 다운로드
     * @param clusterId
     * @param namespaceName
     * @param podName
     * @return
     */
    public ResponseEntity<ByteArrayResource> getLogDownloadFile(Long clusterId, String namespaceName, String podName) {
    	ResponseEntity<ByteArrayResource> result = podAdapterService.getLogDownloadFile(clusterId, namespaceName, podName);
    	return result;
    }
    
    /**
     * Yaml 반환.
     * @param clusterId
     * @param namespaceName
     * @param podName
     * @return
     */
    public String getPodtYaml(Long clusterId, String namespaceName, String podName) {
        String yaml = podAdapterService.getYaml(clusterId, namespaceName, podName);
        yaml = Base64Util.encode(yaml);
        return yaml;
    }
    
    
    public List<PodDto.ResListDto> getPodListByOwner(ApiOwnerSearchParam searchParam) {
		String nodeName = searchParam.getNodeName();
		String ownerUid = searchParam.getOwnerUid();
		String namespace = searchParam.getNamespaceName();
		Map<String, String> selector = searchParam.getSelector();
		
    	List<Pod> k8sPods = podAdapterService.getList(searchParam.getClusterId(), nodeName, ownerUid, namespace, selector);
    	List<PodDto.ResListDto> dtoList = new ArrayList<>();
    	
    	for(Pod k8sPod : k8sPods) {
    		PodEntity podEntity = PodMapper.INSTANCE.toEntity(k8sPod);
    		PodDto.ResListDto dto = PodDtoMapper.INSTANCE.toResK8sListDto(podEntity);
    		dtoList.add(dto);
    	}    	
    	return dtoList;
    }

	@Override
	protected InNamespaceDomainService getDomainService() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
