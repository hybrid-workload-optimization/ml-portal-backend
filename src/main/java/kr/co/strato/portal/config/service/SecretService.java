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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Secret;
import kr.co.strato.adapter.k8s.configMap.service.ConfigMapAdapterService;
import kr.co.strato.adapter.k8s.secret.service.SecretAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.configMap.service.ConfigMapDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.secret.model.SecretEntity;
import kr.co.strato.domain.secret.service.SecretDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.config.model.ConfigMapDto;
import kr.co.strato.portal.config.model.ConfigMapDtoMapper;
import kr.co.strato.portal.config.model.SecretDto;
import kr.co.strato.portal.config.model.SecretDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SecretService extends ProjectAuthorityService {

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	SecretAdapterService secretAdapterService;
	
	@Autowired
	SecretDomainService secretDomainService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	@Autowired
	ProjectDomainService projectDomainService;
	
	/**
	 * Secret 등록
	 * 
	 * @param secretDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> registerSecret(SecretDto secretDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		ClusterEntity cluster = clusterDomainService.get(secretDto.getClusterIdx());
		
		// k8s - post secret
		List<Secret> secretList = secretAdapterService.create(cluster.getClusterId(), new String(Base64.getDecoder().decode(secretDto.getYaml()), "UTF-8"));
		
		// db - save config map
		List<Long> result = secretList.stream()
				.map(s -> {
					SecretEntity secretEntity = null;
					try {
						secretEntity = toSecretEntity(cluster, s);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return secretDomainService.register(secretEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Secret 목록 조회
	 * 
	 * @param pageable
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page<SecretDto.List> getSecretList(Pageable pageable, SecretDto.Search search) throws Exception {
		
		// get clusterId(kubeConfigId)
		//ClusterEntity cluster = clusterDomainService.get(search.getClusterIdx());
				
		// k8s - get secret list
		/*List<Secret> secret = secretAdapterService.getList(cluster.getClusterId());
		Map<String, Secret> secretMaps = Secret.stream()
				.collect(Collectors.toMap(r1 -> r1.getMetadata().getUid(), r2 -> r2));*/
		
		// db - get secret list
		Page<SecretEntity> secretPage = secretDomainService.getList(pageable, search.getProjectIdx(), search.getClusterIdx(), search.getNamespaceIdx());
        
		List<SecretDto.List> secretList = secretPage.stream()
				.map(s -> {
					return SecretDtoMapper.INSTANCE.toList(s);
				})
				.collect(Collectors.toList());
		
        Page<SecretDto.List> pages = new PageImpl<>(secretList, pageable, secretPage.getTotalElements());
        
		return pages;
	}
	
	/**
	 * Secret 상세 조회
	 * 
	 * @param secretIdx
	 * @return
	 * @throws Exception
	 */
	public SecretDto.Detail getSecret(Long secretIdx, UserDto loginUser) throws Exception {
		SecretEntity secretEntity = secretDomainService.get(secretIdx);
		//Long clusterId			= secretEntity.getNamespace().getCluster().getClusterId();
		//String namespaceName	= secretEntity.getNamespace().getName();
		//String secretName		= secretEntity.getName();
		
		Long clusterIdx = secretEntity.getNamespace().getCluster().getClusterIdx();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
		chechAuthority(projectIdx, loginUser);
		
		// k8s - get secret
		//Secret secret = secretAdapterService.get(clusterId, namespaceName, secretName);
		
		SecretDto.Detail dto = SecretDtoMapper.INSTANCE.toDetail(secretEntity);
		
		String data = "";
		if(!"".equals(dto.getData()) && dto.getData() != null) {
			GsonBuilder builder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
	        Gson gson = builder.create();
	        Map user = gson.fromJson(dto.getData(), Map.class);
	        data = gson.toJson(user);
		} else {
			data = dto.getData();
		}
		
		dto.setData(data);
		dto.setProjectIdx(projectIdx);
		return dto;
	}
	
	/**
	 * Secret Yaml 조회
	 * 
	 * @param secretIdx
	 * @return
	 * @throws Exception
	 */
	public String getSecretYaml(Long secretIdx) throws Exception {
		SecretEntity secretEntity = secretDomainService.get(secretIdx);
		Long clusterId		 = secretEntity.getNamespace().getCluster().getClusterId();
		String namespaceName = secretEntity.getNamespace().getName();
        String secretName = secretEntity.getName();
        
		String yaml = secretAdapterService.getYaml(clusterId, namespaceName, secretName);
		
		return Base64.getEncoder().encodeToString(yaml.getBytes());
	}
	
	/**
	 * Secret 수정
	 * 
	 * @param secretIdx
	 * @param secretDto
	 * @return
	 * @throws Exception
	 */
	public List<Long> updateSecret(Long secretIdx, SecretDto secretDto) throws Exception {
		
		// get clusterId(kubeConfigId)
		SecretEntity secret = secretDomainService.get(secretIdx);
		ClusterEntity cluster = secret.getNamespace().getCluster();
		
		// k8s - post secret
		List<Secret> secretList = secretAdapterService.create(cluster.getClusterId(), new String(Base64.getDecoder().decode(secretDto.getYaml()), "UTF-8"));
		
		// db - save config map
		List<Long> result = secretList.stream()
				.map(s -> {
					SecretEntity secertEntity = null;
					try {
						secertEntity = toSecretEntity(cluster, s);
						secertEntity.setId(secretIdx);
					} catch (PortalException e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getErrorType().getDetail());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new PortalException(e.getMessage());
					}
					return secretDomainService.register(secertEntity);
				})
				.collect(Collectors.toList());
		
		return result;
	}
	
	/**
	 * Secret 삭제
	 * 
	 * @param secretIdx
	 * @throws Exception
	 */
	public void deleteSecret(Long secretIdx) throws Exception {
		SecretEntity secretEntity = secretDomainService.get(secretIdx);
		
		Long clusterId			= secretEntity.getNamespace().getCluster().getClusterId();
		String namespaceName	= secretEntity.getNamespace().getName();
        String secretName		= secretEntity.getName();
        
		boolean isDeleted = secretAdapterService.delete(clusterId, namespaceName, secretName);
		if (!isDeleted) {
			throw new PortalException("Secret deletion failed");
		}
		
		secretDomainService.delete(secretEntity);
	}
	
	/**
	 * K8S Secret 정보를 Secret Entity 형태로 변환
	 * 
	 * @param clusterEntity
	 * @param d
	 * @return
	 * @throws Exception
	 */
	private SecretEntity toSecretEntity(ClusterEntity clusterEntity, Secret s) throws PortalException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		String name			= s.getMetadata().getName();
        String namespace	= s.getMetadata().getNamespace();
        String uid			= s.getMetadata().getUid();
        String label		= mapper.writeValueAsString(s.getMetadata().getLabels());
        String data			= mapper.writeValueAsString(s.getData());
        String createAt		= s.getMetadata().getCreationTimestamp();
        
        log.debug("toSecretEntity namespace = {}", namespace);
        log.debug("toSecretEntity clusterEntity idx = {}", clusterEntity.getClusterIdx());
        
        // find namespaceIdx
        List<NamespaceEntity> namespaceList = namespaceDomainService.findByNameAndClusterIdx(namespace, clusterEntity);
        if (CollectionUtils.isEmpty(namespaceList)) {
        	throw new PortalException("Can't find namespace : " + namespace);
        }
        
        SecretEntity result = SecretEntity.builder()
                .name(name)
                .uid(uid)
                .label(label)
                .data(data)
                .createdAt(DateUtil.convertDateTime(createAt))
                .namespace(namespaceList.get(0))
                .build();

        return result;
	}
}
