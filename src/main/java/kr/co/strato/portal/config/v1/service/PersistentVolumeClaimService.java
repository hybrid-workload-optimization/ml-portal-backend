package kr.co.strato.portal.config.v1.service;

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

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.persistentVolumeClaim.service.PersistentVolumeClaimAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.config.v1.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.v1.model.PersistentVolumeClaimDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersistentVolumeClaimService extends InNamespaceService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	PersistentVolumeClaimAdapterService persistentVolumeClaimAdapterService;
	
	@Autowired
	PersistentVolumeClaimDomainService persistentVolumeClaimDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectAuthorityService projectAuthorityService;
	
	/**
	 * Persistent Volume Claim 등록
	 * 
	 * @param persistentVolumeClaimDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerPersistentVolumeClaim(PersistentVolumeClaimDto persistentVolumeClaimDto) throws Exception {
		
		//이름 중복 채크
		duplicateCheckResourceCreation(persistentVolumeClaimDto.getClusterIdx(), persistentVolumeClaimDto.getYaml());
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(persistentVolumeClaimDto.getClusterIdx());
		
		String yaml = new String(Base64.getDecoder().decode(persistentVolumeClaimDto.getYaml()), "UTF-8");
		
		// k8s - post persistent volume claim
		List<PersistentVolumeClaim> persistentVolumeClaimList = persistentVolumeClaimAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save persistent volume claim
		List<Long> result = persistentVolumeClaimList.stream()
				.map(p -> {
					PersistentVolumeClaimEntity persistentVolumeClaimEntity = null;
					try {
						persistentVolumeClaimEntity = toPersistentVolumeClaimEntity(cluster, p);
						persistentVolumeClaimEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return persistentVolumeClaimDomainService.register(persistentVolumeClaimEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Persistent Volume Claim 목록 조회
	 * 
	 * @param pageable
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page<PersistentVolumeClaimDto.List> getPersistentVolumeClaimList(Pageable pageable, PersistentVolumeClaimDto.Search search) throws Exception {
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(search.getClusterIdx());
				
		// k8s - get persistent volume claim list
		List<PersistentVolumeClaim> persistentVolumeClaims = persistentVolumeClaimAdapterService.getList(cluster.getClusterId());
		Map<String, PersistentVolumeClaim> persistentVolumeClaimMaps = persistentVolumeClaims.stream()
				.collect(Collectors.toMap(r1 -> r1.getMetadata().getUid(), r2 -> r2));
		
		// db - get daemon set list
		Page<PersistentVolumeClaimEntity> persistentVolumeClaimPage = persistentVolumeClaimDomainService.getList(pageable, search.getProjectIdx(), search.getClusterIdx(), search.getNamespaceIdx());
        
		List<PersistentVolumeClaimDto.List> persistentVolumeClaimList = persistentVolumeClaimPage.stream()
				.map(d -> {
					if(persistentVolumeClaimMaps.containsKey(d.getUid())) {
						return PersistentVolumeClaimDtoMapper.INSTANCE.toList(d, persistentVolumeClaimMaps.get(d.getUid()));
					}
					return PersistentVolumeClaimDtoMapper.INSTANCE.toList(d);
				})
				.collect(Collectors.toList());
		
        Page<PersistentVolumeClaimDto.List> pages = new PageImpl<>(persistentVolumeClaimList, pageable, persistentVolumeClaimPage.getTotalElements());
        
		return pages;
	}
	
	/**
	 * Persistent Volume Claim 상세 조회
	 * 
	 * @param persistentVolumeClaimIdx
	 * @return
	 * @throws Exception
	 */
	public PersistentVolumeClaimDto.Detail getPersistentVolumeClaim(Long persistentVolumeClaimIdx, UserDto loginUser) throws Exception {
		PersistentVolumeClaimEntity persistentVolumeClaimEntity = persistentVolumeClaimDomainService.get(persistentVolumeClaimIdx);
		Long clusterId											= persistentVolumeClaimEntity.getNamespace().getCluster().getClusterId();
		String namespaceName									= persistentVolumeClaimEntity.getNamespace().getName();
		String persistentVolumeClaimName						= persistentVolumeClaimEntity.getName();
		
		Long clusterIdx = persistentVolumeClaimEntity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
		
		
		// k8s - get Persistent Volume Claim
		PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimAdapterService.get(clusterId, namespaceName, persistentVolumeClaimName);
		
		PersistentVolumeClaimDto.Detail dto = PersistentVolumeClaimDtoMapper.INSTANCE.toDetail(persistentVolumeClaimEntity, persistentVolumeClaim);
		dto.setProjectIdx(projectIdx);
		return dto;
	}
	
	public PersistentVolumeClaimDto.Detail getPersistentVolumeClaim(Long clusterIdx, String namespace, String pvcName) throws Exception {
		PersistentVolumeClaimEntity persistentVolumeClaimEntity = persistentVolumeClaimDomainService.getPersistentVolumeClaim(clusterIdx, namespace, pvcName);
		if(persistentVolumeClaimEntity != null) {
			// k8s - get Persistent Volume Claim
			PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimAdapterService.get(clusterIdx, namespace, pvcName);
			
			ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
			Long projectIdx = projectEntity.getId();
			
			
			PersistentVolumeClaimDto.Detail dto = PersistentVolumeClaimDtoMapper.INSTANCE.toDetail(persistentVolumeClaimEntity, persistentVolumeClaim);
			dto.setProjectIdx(projectIdx);
			return dto;
		}
		return null;
	}
	
	/**
	 * Persistent Volume Claim Yaml 조회
	 * 
	 * @param persistentVolumeClaimIdx
	 * @return
	 * @throws Exception
	 */
	public String getPersistentVolumeClaimYaml(Long persistentVolumeClaimIdx) throws Exception {
		PersistentVolumeClaimEntity persistentVolumeClaimEntity = persistentVolumeClaimDomainService.get(persistentVolumeClaimIdx);
		String yaml = persistentVolumeClaimEntity.getYaml();
		if(yaml == null) {
			Long clusterId					= persistentVolumeClaimEntity.getNamespace().getCluster().getClusterId();
			String namespaceName			= persistentVolumeClaimEntity.getNamespace().getName();
	        String persistentVolumeClaimName= persistentVolumeClaimEntity.getName();
	        
			yaml = persistentVolumeClaimAdapterService.getYaml(clusterId, namespaceName, persistentVolumeClaimName);
		}
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
	
	/**
	 * Persistent Volume Claim 수정
	 * 
	 * @param persistentVolumeClaimIdx
	 * @param persistentVolumeClaimDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> updatePersistentVolumeClaim(Long persistentVolumeClaimIdx, PersistentVolumeClaimDto persistentVolumeClaimDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		PersistentVolumeClaimEntity persistentVolumeClaim = persistentVolumeClaimDomainService.get(persistentVolumeClaimIdx);
		ClusterEntity cluster = persistentVolumeClaim.getNamespace().getCluster();
		
		String yaml = new String(Base64.getDecoder().decode(persistentVolumeClaimDto.getYaml()), "UTF-8");
		
		// k8s - post persistent volume claim
		List<PersistentVolumeClaim> persistentVolumeClaimList = persistentVolumeClaimAdapterService.create(cluster.getClusterId(), yaml);
		
		// db - save persistent volume claim
		List<Long> result = persistentVolumeClaimList.stream()
				.map(d -> {
					PersistentVolumeClaimEntity persistentVolumeClaimEntity = null;
					try {
						persistentVolumeClaimEntity = toPersistentVolumeClaimEntity(cluster, d);
						persistentVolumeClaimEntity.setId(persistentVolumeClaimIdx);
						persistentVolumeClaimEntity.setYaml(yaml);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return persistentVolumeClaimDomainService.register(persistentVolumeClaimEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Persistent Volume Claim 삭제
	 * 
	 * @param persistentVolumeClaimIdx
	 * @throws Exception
	 */
	public void deletePersistentVolumeClaim(Long persistentVolumeClaimIdx) throws Exception {
		PersistentVolumeClaimEntity persistentVolumeClaimEntity = persistentVolumeClaimDomainService.get(persistentVolumeClaimIdx);
		
		Long clusterId						= persistentVolumeClaimEntity.getNamespace().getCluster().getClusterId();
		String namespaceName				= persistentVolumeClaimEntity.getNamespace().getName();
        String persistentVolumeClaimName	= persistentVolumeClaimEntity.getName();
        
		boolean isDeleted = persistentVolumeClaimAdapterService.delete(clusterId, namespaceName, persistentVolumeClaimName);
		if (!isDeleted) {
			throw new PortalException("Persistent Volume Claim deletion failed");
		}
		
		persistentVolumeClaimDomainService.delete(persistentVolumeClaimEntity);
	}
	
	/**
	 * K8S Persistent Volume Claim 정보를Persistent Volume Claim Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param d
	 * @return
	 * @throws Exception
	 */
	private PersistentVolumeClaimEntity toPersistentVolumeClaimEntity(ClusterEntity clusterEntity, PersistentVolumeClaim p) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		String name			= p.getMetadata().getName();
        String namespace	= p.getMetadata().getNamespace();
        String uid			= p.getMetadata().getUid();
        // TODO : 이미지는 여러개 존재할 수 있는 있으므로 추후 관련 내용을 보완해야 함 
        String label		= mapper.writeValueAsString(p.getMetadata().getLabels());
        String stauts		= p.getStatus().getPhase();
        String accessType = null;
        List<String> accessList = p.getSpec().getAccessModes();
        if(accessList != null && accessList.size() > 0) {
        	for(int i = 0; i < accessList.size(); i++) {
        		if(i == 0) {
        			accessType = accessList.get(i);
        		} else {
        			accessType = accessType + ", " + accessList.get(i);
        		}
        	}
        }
        String storageClass = p.getSpec().getStorageClassName();
        String createAt		= p.getMetadata().getCreationTimestamp();
        
        String storageCapacity = null;
        Map<String, Quantity> capacity = p.getStatus().getCapacity();
        if(capacity != null) {
        	Quantity quantity = capacity.get("storage");
            if(quantity != null) {
            	String storageAmount = quantity.getAmount();
            	String formatAmount = quantity.getFormat();
            	storageCapacity = storageAmount + formatAmount;
            	storageCapacity.replaceAll("\"", "");
            }
        }
        
        
        String storageRequest = null;
        Map<String, Quantity> request = p.getSpec().getResources().getRequests();
        if(request != null) {
        	Quantity reqStorage = request.get("storage");
            if(reqStorage != null) {
            	String storageAmount = reqStorage.getAmount();
            	String formatAmount = reqStorage.getFormat();
            	storageRequest = storageAmount + formatAmount;
            	storageRequest = storageRequest.replaceAll("\"", "");
            }
        }
        
        log.debug("toPersistentVolumeClaimEntity namespace = {}", namespace);
        log.debug("toPersistentVolumeClaimEntity clusterEntity idx = {}", clusterEntity.getClusterIdx());
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        PersistentVolumeClaimEntity result = PersistentVolumeClaimEntity.builder()
                .name(name)
                .uid(uid)
                //.status(stauts)
                .storageCapacity(storageCapacity)
                .storageRequest(storageRequest)
                .accessType(accessType)
                .storageClass(storageClass)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}

	@Override
	protected InNamespaceDomainService getDomainService() {
		return persistentVolumeClaimDomainService;
	}
}
