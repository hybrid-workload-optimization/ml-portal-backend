package kr.co.strato.portal.workload.service;

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

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import kr.co.strato.adapter.k8s.daemonset.service.DaemonSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.daemonset.service.DaemonSetDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.model.DaemonSetDto;
import kr.co.strato.portal.workload.model.DaemonSetDtoMapper;
import kr.co.strato.portal.workload.model.PodDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DaemonSetService extends InNamespaceService {

	@Autowired
	DaemonSetAdapterService daemonSetAdapterService;
	
	@Autowired
	DaemonSetDomainService daemonSetDomainService;
	
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
	 * Daemon Set 목록 조회
	 * 
	 * @param pageable
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page<DaemonSetDto.List> getDaemonSetList(Pageable pageable, DaemonSetDto.Search search) throws Exception {
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(search.getClusterIdx());
				
		// k8s - get daemon set list
		List<DaemonSet> daemonSets = daemonSetAdapterService.getList(cluster.getClusterId());
		Map<String, DaemonSet> daemonSetMaps = daemonSets.stream()
				.collect(Collectors.toMap(r1 -> r1.getMetadata().getUid(), r2 -> r2));
		
		// db - get daemon set list
		Page<DaemonSetEntity> daemonSetPage = daemonSetDomainService.getList(pageable, search.getProjectIdx(), search.getClusterIdx(), search.getNamespaceIdx());
        
		List<DaemonSetDto.List> daemonSetList = daemonSetPage.stream()
				.map(d -> {
					if(daemonSetMaps.containsKey(d.getDaemonSetUid())) {
						return DaemonSetDtoMapper.INSTANCE.toList(d, daemonSetMaps.get(d.getDaemonSetUid()));
					}
					return DaemonSetDtoMapper.INSTANCE.toList(d);
				})
				.collect(Collectors.toList());
		
        Page<DaemonSetDto.List> pages = new PageImpl<>(daemonSetList, pageable, daemonSetPage.getTotalElements());
        
		return pages;
	}
	
	/**
	 * Daemon Set 등록
	 * 
	 * @param daemonSetDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerDaemonSet(DaemonSetDto daemonSetDto) throws Exception {
		
		//이름 중복 채크.
		duplicateCheckResourceCreation(daemonSetDto.getClusterIdx(), daemonSetDto.getYaml());
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(daemonSetDto.getClusterIdx());
		
		String yaml = new String(Base64.getDecoder().decode(daemonSetDto.getYaml()), "UTF-8");
		
		// k8s - post daemon set
		List<DaemonSet> daemonSetList = daemonSetAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save daemon set
		List<Long> result = daemonSetList.stream()
				.map(d -> {
					DaemonSetEntity daemonSetEntity = null;
					try {
						daemonSetEntity = toDaemonSetEntity(cluster, d);
						daemonSetEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return daemonSetDomainService.register(daemonSetEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Daemon Set 상세 조회
	 * 
	 * @param daemonSetIdx
	 * @return
	 * @throws Exception
	 */
	public DaemonSetDto.Detail getDaemonSet(Long daemonSetIdx, UserDto loginUser) throws Exception {
		DaemonSetEntity daemonSetEntity = daemonSetDomainService.get(daemonSetIdx);
		Long clusterId			= daemonSetEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= daemonSetEntity.getNamespace().getName();
		String daemonSetName	= daemonSetEntity.getDaemonSetName();
		
		Long clusterIdx = daemonSetEntity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
		
		// k8s - get daemon set
		DaemonSet daemonSet = daemonSetAdapterService.get(clusterId, namespaceName, daemonSetName);
		
		DaemonSetDto.Detail result = DaemonSetDtoMapper.INSTANCE.toDetail(daemonSetEntity, daemonSet);
		
		// k8s - get pod list by daemon set
		PodDto.OwnerSearchParam searchParam = new PodDto.OwnerSearchParam();
		searchParam.setOwnerUid(result.getUid());
		
		@SuppressWarnings("unchecked")
		ArrayList<PodDto.ResListDto> pods = (ArrayList)podService.getPodOwnerPodList(clusterId, searchParam);
		
		result.setPods(pods);
		result.setProjectIdx(projectIdx);
		return result;
	}
	
	/**
	 * Daemon Set Yaml 조회
	 * 
	 * @param daemonSetIdx
	 * @return
	 * @throws Exception
	 */
	public String getDaemonSetYaml(Long daemonSetIdx) throws Exception {
		DaemonSetEntity daemonSetEntity = daemonSetDomainService.get(daemonSetIdx);
		String yaml = daemonSetEntity.getYaml();
		if(yaml == null) {
			Long clusterId			= daemonSetEntity.getNamespace().getCluster().getClusterId();
			String namespaceName	= daemonSetEntity.getNamespace().getName();
	        String daemonSetName	= daemonSetEntity.getDaemonSetName();
	        
			yaml = daemonSetAdapterService.getYaml(clusterId, namespaceName, daemonSetName);
		}		
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
	
	/**
	 * Daemon Set 수정
	 * 
	 * @param daemonSetIdx
	 * @param daemonSetDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> updateDaemonSet(Long daemonSetIdx, DaemonSetDto daemonSetDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		DaemonSetEntity daemonSet = daemonSetDomainService.get(daemonSetIdx);
		ClusterEntity cluster = daemonSet.getNamespace().getCluster();
		
		
		String yaml = new String(Base64.getDecoder().decode(daemonSetDto.getYaml()), "UTF-8");
		
		// k8s - post replica set
		List<DaemonSet> daemonSetList = daemonSetAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save replica set
		List<Long> result = daemonSetList.stream()
				.map(d -> {
					DaemonSetEntity daemonSetEntity = null;
					try {
						daemonSetEntity = toDaemonSetEntity(cluster, d);
						daemonSetEntity.setDaemonSetIdx(daemonSetIdx);
						daemonSetEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return daemonSetDomainService.register(daemonSetEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Daemon Set 삭제
	 * 
	 * @param daemonSetIdx
	 * @throws Exception
	 */
	public void deleteDaemonSet(Long daemonSetIdx) throws Exception {
		DaemonSetEntity daemonSetEntity = daemonSetDomainService.get(daemonSetIdx);
		Long clusterId			= daemonSetEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= daemonSetEntity.getNamespace().getName();
        String daemonSetName	= daemonSetEntity.getDaemonSetName();
        
		boolean isDeleted = daemonSetAdapterService.delete(clusterId, namespaceName, daemonSetName);
		if (!isDeleted) {
			throw new PortalException("DaemonSet deletion failed");
		}
		
		daemonSetDomainService.delete(daemonSetEntity);
	}
	
	/**
	 * K8S Daemon Set 정보를 Daemon Set Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param d
	 * @return
	 * @throws Exception
	 */
	private DaemonSetEntity toDaemonSetEntity(ClusterEntity clusterEntity, DaemonSet d) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		String name			= d.getMetadata().getName();
        String namespace	= d.getMetadata().getNamespace();
        String uid			= d.getMetadata().getUid();
        // TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
        String image		= d.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String selector		= mapper.writeValueAsString(d.getSpec().getSelector().getMatchLabels());
        String label		= mapper.writeValueAsString(d.getMetadata().getLabels());
        String annotations	= mapper.writeValueAsString(d.getMetadata().getAnnotations());
        String createAt		= d.getMetadata().getCreationTimestamp();
        
        log.debug("toDaemonSetEntity namespace = {}", namespace);
        log.debug("toDaemonSetEntity clusterEntity idx = {}", clusterEntity.getClusterIdx());
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        System.out.println("Namespace List === " + namespaceList);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        DaemonSetEntity result = DaemonSetEntity.builder()
                .daemonSetName(name)
                .daemonSetUid(uid)
                .image(image)
                .selector(selector)
                .label(label)
                .annotation(annotations)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}

	@Override
	protected InNamespaceDomainService getDomainService() {
		return daemonSetDomainService;
	}
}