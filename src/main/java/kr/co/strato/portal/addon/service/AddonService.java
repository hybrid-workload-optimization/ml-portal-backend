package kr.co.strato.portal.addon.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.adapter.k8s.common.proxy.AddonProxy;
import kr.co.strato.domain.addon.model.AddonEntity;
import kr.co.strato.domain.addon.service.AddonDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.addon.model.Addon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddonService {
	
	@Autowired
	private AddonDomainService addonDomainService;
	
	@Autowired
    private ClusterDomainService clusterDomainService;
	
	@Autowired
	private AddonProxy addonProxy;
	
	
	
	/**
	 * Kubelet 버전에 맞는 Addon 리스트 반환.
	 * @param kubeConfigId
	 * @return
	 * @throws IOException
	 */
	public List<Addon> getAddons(Long clusterId) {
		Long kubeConfigId = getKubeConfigId(clusterId);
		
		List<Addon> list = addonProxy.getAddons(kubeConfigId);
		if(list.size() > 0) {
			List<AddonEntity> entitys = addonDomainService.getListByClusterId(clusterId);
			Map<String, AddonEntity> addonMap = entitys.stream()
					.collect(Collectors.toMap(
							AddonEntity::getAddonId,
				    		Function.identity()));
			
			list.stream().forEach(
					addon -> addon.setAddonEntity(addonMap.get(addon.getAddonId())));
		}
		log.info("Addon list call. clusterIdx: {}", clusterId);
		log.info("Addon list size: {}", list.size());
		return list;
	}
	
	/**
	 * Addon 디테일 정보 반환.
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public Addon getAddon(Long clusterId, String addonId) {
		Long kubeConfigId = getKubeConfigId(clusterId);
		Addon addon = addonProxy.getAddon(kubeConfigId, addonId);
		
		AddonEntity entity = addonDomainService.getEntity(clusterId, addonId);
		addon.setAddonEntity(entity);
		return addon;
	}
	
	/**
	 * Addon 설치
	 * @param kubeConfigId
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public boolean installAddon(Long clusterId, String addonId, Map<String, Object> parameters, String userName) {
		Addon addon = getAddon(clusterId, addonId);
		if(addon == null) {
			return false;
		}
		
		Long kubeConfigId = getKubeConfigId(clusterId);
		
		Map<String, Object> param = new HashMap<>();
		param.put("kubeConfigId", kubeConfigId);
		param.put("addonId", addonId);
		param.put("parameters", parameters);
		
		boolean isOk = addonProxy.install(param);
		if(isOk) {
			AddonEntity entity = new AddonEntity();
			entity.setClusterIdx(clusterId);
			entity.setAddonId(addon.getAddonId());
			entity.setAddonType(addon.getAddonType());
			entity.setInstallUserId(addonId);
			addonDomainService.register(entity);
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		log.info("Addon install. clusterIdx: {}, addonId: {}", clusterId, addonId);
		log.info("Addon install. parameters: {}", gson.toJson(parameters));
		log.info("Addon install. result: {}", isOk);
		return isOk;
	}
	
	/**
	 * Addon 삭제.
	 * @param kubeConfigId
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public boolean uninstallAddon(Long clusterId, String addonId) {
		Long kubeConfigId = getKubeConfigId(clusterId);
		
		Map<String, Object> param = new HashMap<>();
		param.put("kubeConfigId", kubeConfigId);
		param.put("addonId", addonId);
		
		boolean isOk = addonProxy.uninstall(param);
		if(isOk) {
			addonDomainService.delete(clusterId, addonId);
		}
		
		log.info("Addon uninstall. clusterIdx: {}, addonId: {}", clusterId, addonId);
		log.info("Addon uninstall result: {}", isOk);
		return isOk;
	}
	
	/**
	 * Addon 설치 여부 리턴.
	 * @param clusterIdx
	 * @param addonType
	 * @return
	 */
	public boolean isInstall(Long clusterIdx, String addonType) {
		return addonDomainService.getEntityByType(clusterIdx, addonType) != null;
	}
	
	public Addon getAddonByType(Long clusterIdx, String addonType) {
		AddonEntity entity = addonDomainService.getEntityByType(clusterIdx, addonType);
		if(entity != null) {
			Long kubeConfigId = getKubeConfigId(clusterIdx);
			Addon addon = addonProxy.getAddon(kubeConfigId, entity.getAddonId());
			
			addon.setAddonEntity(entity);
			return addon;
		}
		return null;
	}
	
	/**
	 * Cluster의 KubeConfigId 반환.
	 * @param clusterIdx
	 * @return
	 */
	private Long getKubeConfigId(Long clusterIdx) {
		ClusterEntity clusterEntity =  clusterDomainService.get(clusterIdx);
		return clusterEntity.getClusterId();
	}
}
