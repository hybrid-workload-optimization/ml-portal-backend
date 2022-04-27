package kr.co.strato.portal.config.service;

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

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.adapter.k8s.configMap.service.ConfigMapAdapterService;
import kr.co.strato.adapter.k8s.persistentVolumeClaim.service.PersistentVolumeClaimAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.configMap.service.ConfigMapDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.service.PersistentVolumeClaimDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.config.model.ConfigMapDto;
import kr.co.strato.portal.config.model.ConfigMapDtoMapper;
import kr.co.strato.portal.config.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.model.PersistentVolumeClaimDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfigMapService extends ProjectAuthorityService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ConfigMapAdapterService configMapAdapterService;
	
	@Autowired
	ConfigMapDomainService configMapDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	/**
	 * Config Map 등록
	 * 
	 * @param configMapDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerConfigMap(ConfigMapDto configMapDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(configMapDto.getClusterIdx());
		
		// k8s - post config map
		List<ConfigMap> configMapList = configMapAdapterService.create(cluster.getClusterId(), new String(Base64.getDecoder().decode(configMapDto.getYaml()), "UTF-8"));
		
		// db - save config map
		List<Long> result = configMapList.stream()
				.map(c -> {
					ConfigMapEntity configMapEntity = null;
					try {
						configMapEntity = toConfigMapEntity(cluster, c);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return configMapDomainService.register(configMapEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Config Map 목록 조회
	 * 
	 * @param pageable
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page<ConfigMapDto.List> getConfigMapList(Pageable pageable, ConfigMapDto.Search search) throws Exception {
		
		// get clusterId(kubeConfigId)
		//ClusterEntity cluster = clusterDomainService.get(search.getClusterIdx());
				
		// k8s - get config map list
		/*List<ConfigMap> configMap = configMapAdapterService.getList(cluster.getClusterId());
		Map<String, ConfigMap> configMapMaps = configMap.stream()
				.collect(Collectors.toMap(r1 -> r1.getMetadata().getUid(), r2 -> r2));*/
		
		// db - get config map list
		Page<ConfigMapEntity> configMapPage = configMapDomainService.getList(pageable, search.getProjectIdx(), search.getClusterIdx(), search.getNamespaceIdx());
        
		List<ConfigMapDto.List> configMapList = configMapPage.stream()
				.map(c -> {
					return ConfigMapDtoMapper.INSTANCE.toList(c);
				})
				.collect(Collectors.toList());
		
        Page<ConfigMapDto.List> pages = new PageImpl<>(configMapList, pageable, configMapPage.getTotalElements());
        
		return pages;
	}
	
	/**
	 * Config Map 상세 조회
	 * 
	 * @param configMapIdx
	 * @return
	 * @throws Exception
	 */
	public ConfigMapDto.Detail getConfigMap(Long configMapIdx, UserDto loginUser) throws Exception {
		ConfigMapEntity configMapEntity = configMapDomainService.get(configMapIdx);
System.out.println("configMapEntity === " + configMapEntity.getData());		
		Long clusterId			= configMapEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= configMapEntity.getNamespace().getName();
		String configMapName	= configMapEntity.getName();
		
		Long clusterIdx = configMapEntity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		chechAuthority(projectIdx, loginUser);
		
		// k8s - get Config Map
		//ConfigMap configMap = configMapAdapterService.get(clusterId, namespaceName, configMapName);
		
		ConfigMapDto.Detail dto = ConfigMapDtoMapper.INSTANCE.toDetail(configMapEntity);
		System.out.println("data === " + dto.getData());
		dto.setProjectIdx(projectIdx);
		return dto;
	}
	
	/**
	 * Config Map Yaml 조회
	 * 
	 * @param configMapIdx
	 * @return
	 * @throws Exception
	 */
	public String getConfigMapYaml(Long configMapIdx) throws Exception {
		ConfigMapEntity configMapEntity = configMapDomainService.get(configMapIdx);
		Long clusterId		 = configMapEntity.getNamespace().getCluster().getClusterId();
		String namespaceName = configMapEntity.getNamespace().getName();
        String configMapName = configMapEntity.getName();
        
		String yaml = configMapAdapterService.getYaml(clusterId, namespaceName, configMapName);
		
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
	
	/**
	 * Config Map 수정
	 * 
	 * @param configMapIdx
	 * @param configMapDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> updateConfigMap(Long configMapIdx, ConfigMapDto configMapDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		ConfigMapEntity configMap = configMapDomainService.get(configMapIdx);
		ClusterEntity cluster = configMap.getNamespace().getCluster();
		
		// k8s - post config map
		List<ConfigMap> configMapList = configMapAdapterService.create(cluster.getClusterId(), new String(Base64.getDecoder().decode(configMapDto.getYaml()), "UTF-8"));
		
		// db - save config map
		List<Long> result = configMapList.stream()
				.map(c -> {
					ConfigMapEntity configMapEntity = null;
					try {
						configMapEntity = toConfigMapEntity(cluster, c);
						configMapEntity.setId(configMapIdx);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return configMapDomainService.register(configMapEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Config Map 삭제
	 * 
	 * @param configMapIdx
	 * @throws Exception
	 */
	public void deleteConfigMap(Long configMapIdx) throws Exception {
		ConfigMapEntity configMapEntity = configMapDomainService.get(configMapIdx);
		
		Long clusterId			= configMapEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= configMapEntity.getNamespace().getName();
        String configMapName	= configMapEntity.getName();
        
		boolean isDeleted = configMapAdapterService.delete(clusterId, namespaceName, configMapName);
		if (!isDeleted) {
			throw new PortalException("Config Map deletion failed");
		}
		
		configMapDomainService.delete(configMapEntity);
	}
	
	/**
	 * K8S Config Map 정보를 Config Map Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param d
	 * @return
	 * @throws Exception
	 */
	private ConfigMapEntity toConfigMapEntity(ClusterEntity clusterEntity, ConfigMap c) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		String name			= c.getMetadata().getName();
        String namespace	= c.getMetadata().getNamespace();
        String uid			= c.getMetadata().getUid();
        String data			= mapper.writeValueAsString(c.getData());
        String createAt		= c.getMetadata().getCreationTimestamp();
        
        log.debug("toConfigMapEntityEntity namespace = {}", namespace);
        log.debug("toConfigMapEntityEntity clusterEntity idx = {}", clusterEntity.getClusterIdx());
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        ConfigMapEntity result = ConfigMapEntity.builder()
                .name(name)
                .uid(uid)
                .data(data)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}
}
