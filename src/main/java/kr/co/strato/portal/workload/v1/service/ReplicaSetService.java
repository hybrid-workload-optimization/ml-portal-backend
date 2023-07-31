package kr.co.strato.portal.workload.v1.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.adapter.k8s.replicaset.service.ReplicaSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.service.ReplicaSetDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.v1.model.PodDto;
import kr.co.strato.portal.workload.v1.model.ReplicaSetDto;
import kr.co.strato.portal.workload.v1.model.ReplicaSetDtoMapper;
import kr.co.strato.portal.workload.v1.model.ReplicaSetDto.Search;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReplicaSetService extends InNamespaceService {

	@Autowired
	ReplicaSetAdapterService replicaSetAdapterService;
	
	@Autowired
	ReplicaSetDomainService replicaSetDomainService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	PodService podService;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectAuthorityService projectAuthorityService;
	
	/**
	 * Replica Set 목록 조회
	 * 
	 * @param pageable
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page<ReplicaSetDto.List> getReplicaSetList(Pageable pageable, Search search) throws Exception {
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(search.getClusterIdx());
				
		// k8s - get replica set list
		List<ReplicaSet> replicaSets = replicaSetAdapterService.getList(cluster.getClusterId());
		Map<String, ReplicaSet> replicaSetMaps = replicaSets.stream()
				.collect(Collectors.toMap(r1 -> r1.getMetadata().getUid(), r2 -> r2));
		
		// db - get replica set list
		Page<ReplicaSetEntity> replicaSetPage = replicaSetDomainService.getList(pageable, search.getProjectIdx(), search.getClusterIdx(), search.getNamespaceIdx());
        
		List<ReplicaSetDto.List> replicaSetList = replicaSetPage.stream()
				.map(r -> {
					if (replicaSetMaps.containsKey(r.getReplicaSetUid())) {
						return ReplicaSetDtoMapper.INSTANCE.toList(r, replicaSetMaps.get(r.getReplicaSetUid()));
					}
					return ReplicaSetDtoMapper.INSTANCE.toList(r);
				})
				.collect(Collectors.toList());
		
        Page<ReplicaSetDto.List> pages = new PageImpl<>(replicaSetList, pageable, replicaSetPage.getTotalElements());
        
        return pages;
	}
	
	/**
	 * Replica Set 상세 조회
	 * 
	 * @param replicaSetIdx
	 * @return
	 * @throws Exception
	 */
	public ReplicaSetDto.Detail getReplicaSet(Long replicaSetIdx, UserDto loginUser) throws Exception {
		ReplicaSetEntity replicaSetEntity = replicaSetDomainService.get(replicaSetIdx);
		Long clusterId			= replicaSetEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= replicaSetEntity.getNamespace().getName();
		String replicaSetName	= replicaSetEntity.getReplicaSetName();
		
		
		Long clusterIdx = replicaSetEntity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
		
		
		// k8s - get replica set
		ReplicaSet replicaSet = replicaSetAdapterService.get(clusterId, namespaceName, replicaSetName);
		
		ReplicaSetDto.Detail result = ReplicaSetDtoMapper.INSTANCE.toDetail(replicaSetEntity, replicaSet);
		
		// k8s - get pod list by replica set
		PodDto.OwnerSearchParam searchParam = new PodDto.OwnerSearchParam();
		searchParam.setOwnerUid(result.getUid());
		
		@SuppressWarnings("unchecked")
		ArrayList<PodDto.ResListDto> pods = (ArrayList)podService.getPodOwnerPodList(clusterId, searchParam);
		
		result.setPods(pods);
		result.setProjectIdx(projectIdx);
		return result;
	}

	/**
	 * Replica Set 등록
	 * 
	 * @param replicaSetDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerReplicaSet(ReplicaSetDto replicaSetDto) throws Exception {
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(replicaSetDto.getClusterIdx());
		
		//이름 중복 채크
		duplicateCheckResourceCreation(cluster.getClusterId(), replicaSetDto.getYaml());
		
		String yaml = new String(Base64.getDecoder().decode(replicaSetDto.getYaml()), "UTF-8");
		
		// k8s - post replica set
		List<ReplicaSet> replicaSetList = replicaSetAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save replica set
		List<Long> result = replicaSetList.stream()
				.map(r -> {
					ReplicaSetEntity replicaSetEntity = null;
					try {
						replicaSetEntity = toReplicaSetEntity(cluster, r);
						replicaSetEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return replicaSetDomainService.register(replicaSetEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Replica Set 수정
	 * 
	 * @param replicaSetIdx
	 * @param replicaSetDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> updateReplicaSet(Long replicaSetIdx, ReplicaSetDto replicaSetDto) throws Exception {
		// get clusterId(kubeConfigId)
		ReplicaSetEntity replicaSet = replicaSetDomainService.get(replicaSetIdx);
		ClusterEntity cluster = replicaSet.getNamespace().getCluster();
		
		String yaml = new String(Base64.getDecoder().decode(replicaSetDto.getYaml()), "UTF-8");
		
		// k8s - post replica set
		List<ReplicaSet> replicaSetList = replicaSetAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save replica set
		List<Long> result = replicaSetList.stream()
				.map(r -> {
					ReplicaSetEntity replicaSetEntity = null;
					try {
						replicaSetEntity = toReplicaSetEntity(cluster, r);
						replicaSetEntity.setReplicaSetIdx(replicaSetIdx);
						replicaSetEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return replicaSetDomainService.register(replicaSetEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Replica Set 삭제
	 * 
	 * @param replicaSetIdx
	 * @throws Exception
	 */
	public void deleteReplicaSet(Long replicaSetIdx) throws Exception {
		ReplicaSetEntity replicaSetEntity = replicaSetDomainService.get(replicaSetIdx);
		Long clusterId			= replicaSetEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= replicaSetEntity.getNamespace().getName();
        String replicaSetName	= replicaSetEntity.getReplicaSetName();
        
		boolean isDeleted = replicaSetAdapterService.delete(clusterId, namespaceName, replicaSetName);
		if (!isDeleted) {
			throw new PortalException("ReplicaSet deletion failed");
		}
		
		replicaSetDomainService.delete(replicaSetEntity);
	}
	
	/**
	 * K8S Replica Set 정보를 Replica Set Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param r
	 * @return
	 * @throws Exception
	 */
	private ReplicaSetEntity toReplicaSetEntity(ClusterEntity clusterEntity, ReplicaSet r) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

        String name			= r.getMetadata().getName();
        String namespace	= r.getMetadata().getNamespace();
        String uid			= r.getMetadata().getUid();
        // TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
        String image		= r.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String selector		= mapper.writeValueAsString(r.getSpec().getSelector().getMatchLabels());
        String label		= mapper.writeValueAsString(r.getMetadata().getLabels());
        String annotations	= mapper.writeValueAsString(r.getMetadata().getAnnotations());
        String createAt		= r.getMetadata().getCreationTimestamp();
        
        log.debug("toReplicaSetEntity namespace = {}", namespace);
        log.debug("toReplicaSetEntity clusterEntity = {}", clusterEntity.toString());
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        ReplicaSetEntity result = ReplicaSetEntity.builder()
                .replicaSetName(name)
                .replicaSetUid(uid)
                .image(image)
                .selector(selector)
                .label(label)
                .annotation(annotations)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}

	/**
	 * Replica Set Yaml 조회
	 * 
	 * @param replicaSetIdx
	 * @return
	 * @throws Exception
	 */
	public String getReplicaSetYaml(Long replicaSetIdx) throws Exception {
		ReplicaSetEntity replicaSetEntity = replicaSetDomainService.get(replicaSetIdx);
		String yaml = replicaSetEntity.getYaml();
		if(yaml == null) {
			Long clusterId			= replicaSetEntity.getNamespace().getCluster().getClusterId();
			String namespaceName	= replicaSetEntity.getNamespace().getName();
	        String replicaSetName	= replicaSetEntity.getReplicaSetName();
	        
			yaml = replicaSetAdapterService.getYaml(clusterId, namespaceName, replicaSetName);
		}
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}

	@Override
	protected InNamespaceDomainService getDomainService() {
		return replicaSetDomainService;
	}

}
