package kr.co.strato.portal.addon.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.common.proxy.CommonProxy;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.addon.model.AddonEntity;
import kr.co.strato.domain.addon.service.AddonDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.FileUtils;
import kr.co.strato.portal.addon.adapter.AddonAdapter;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.model.Version;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddonService {

	private static final String ADDON_PATH = "classpath:/addons/meta/*.json";

	@Autowired
	private AddonDomainService addonDomainService;

	@Autowired
	private ClusterDomainService clusterDomainService;

	@Autowired
	private ServiceAdapterService serviceAdapterService;

	@Autowired
	private NodeAdapterService nodeAdapterService;

	@Autowired
	private CommonProxy commonProxy;

	private static Map<String, Addon> addons;
	
	@Value("${spring.profiles.active:}") 
	private String activeProfile;

	/**
	 * Kubelet 버전에 맞는 Addon 리스트 반환.
	 * 
	 * @param kubeConfigId
	 * @return
	 * @throws IOException
	 */
	public List<Addon> getAddons(Long clusterIdx) throws IOException {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

		String v = clusterEntity.getProviderVersion();
		if(v.contains("-")) {
			v = v.substring(0, v.indexOf("-"));
		}
		
		Version kubeletVersion = new Version(v);

		List<Addon> list = new ArrayList<>();
		Collection<Addon> addons = getAddons().values();
		for (Addon addon : addons) {
			boolean isSupported = true;

			String minVersion = addon.getRequiredSpec().getMinKubeletVersion();
			String maxVersion = addon.getRequiredSpec().getMaxKubeletVersion();

			if (minVersion != null) {
				isSupported = kubeletVersion.compareTo(new Version(minVersion)) != -1;
			}

			if (maxVersion != null) {
				isSupported = kubeletVersion.compareTo(new Version(maxVersion)) != 1;
			}
			
			if(addon.getProfile() != null) { 
				String profile = activeProfile;
				if(profile == null || profile.equals("")) {
					profile = "default";
				}
				isSupported = profile.equals(addon.getProfile());
			}

			if (isSupported) {
				list.add(addon);
			}
			
			
		}

		if (list.size() > 0) {
			List<AddonEntity> entitys = addonDomainService.getListByClusterId(clusterIdx);
			Map<String, AddonEntity> addonMap = entitys.stream()
					.collect(Collectors.toMap(AddonEntity::getAddonId, Function.identity()));

			list.stream().forEach(addon -> addon.setAddonEntity(addonMap.get(addon.getAddonId())));
		}
		log.info("Addon list call. clusterIdx: {}", clusterIdx);
		log.info("Addon list size: {}", list.size());
		return list;
	}

	/**
	 * Addon 디테일 정보 반환.
	 * 
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public Addon getAddon(Long clusterIdx, String addonId) throws IOException {
		Addon addon = getAddons().get(addonId);
		if (addon == null) {
			log.error("Addon not found. - addonId: {}", addonId);
			return null;
		}

		AddonAdapter adapter = getAdapter(addon);
		if (adapter != null) {
			// 상세 정보 셋
			ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
			adapter.setDetails(this, clusterEntity, addon);
		}

		AddonEntity entity = addonDomainService.getEntity(clusterIdx, addonId);
		addon.setAddonEntity(entity);

		return addon;

	}

	/**
	 * Addon 설치
	 * 
	 * @param kubeConfigId
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public boolean installAddon(Long clusterIdx, String addonId, Map<String, Object> parameters, String userId)
			throws IOException {

		Addon addon = getAddon(clusterIdx, addonId);
		if (addon == null) {
			return false;
		}

		Long kubeConfigId = getKubeConfigId(clusterIdx);

		log.info("Addon install start. - KubeConfigId: {}", kubeConfigId);
		log.info("Addon install start. - addonId: {}", addonId);
		log.info("Addon install start. - addon name: {}", addon.getName());

		List<String> yamls = addon.getYamls();
		List<String> result = new ArrayList<>();
		AddonAdapter adapter = getAdapter(addon);

		KubernetesClient client = new DefaultKubernetesClient();
		boolean isOk = true;
		for (String yamlPath : yamls) {
			ClassPathResource resource = new ClassPathResource(yamlPath);
			InputStream is = resource.getInputStream();

			List<HasMetadata> resoures = client.load(is).get();
			if (adapter != null && parameters != null) {
				// 파라메타 반영.
				adapter.setParameter(resoures, parameters);
			}

			String yamlString = null;
			for (HasMetadata data : resoures) {
				yamlString = Serialization.asYaml(data);

				YamlApplyParam param = YamlApplyParam.builder()
						.kubeConfigId(kubeConfigId)
						.yaml(yamlString)
						.namespace("argocd")
						.build();
				
				try {
					String str = commonProxy.apply(param);					
					if (str != null && str.length() > 0) {
						
						String kind = data.getKind();
						String name = data.getMetadata().getName();
						log.info("Addon install success. - kind: {}, name: {}", kind, name);
					}
					result.add(str);
				} catch (Exception e) {
					log.error("Addon install fail.");
					String printYaml = yamlString;
					if(printYaml.length() > 2000) {
						printYaml = printYaml.substring(0, 2000);
						printYaml += ".....";
					}
					log.error(printYaml);
					log.error("", e);
				}
				
			}
			/*
			if (resoures.size() != applyList.size()) {
				isOk = false;
				break;
			}
			*/
		}

		if (!isOk) {
			// 설치 실패한 경우 rollback
			log.info("Addon install fail. - KubeConfigId: {}", kubeConfigId);
			log.info("Addon install fail. - addonId: {}", addonId);
			log.info("Addon install fail. - addon name: {}", addon.getName());

			for (String s : result) {
				YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(s).build();

				commonProxy.delete(param);
				log.info("Addon install rollback - delete resource yaml: {}", s);
			}
		} else {
			// 설치 성공한 경우 디비 저장.
			AddonEntity entity = new AddonEntity();
			entity.setClusterIdx(clusterIdx);
			entity.setAddonId(addon.getAddonId());
			entity.setAddonType(addon.getAddonType());
			entity.setInstallUserId(userId);
			addonDomainService.register(entity);
		}
		client.close();
		log.info("Addon install finish. - result: {}", isOk);
		return isOk;
	}

	/**
	 * Addon 삭제.
	 * 
	 * @param kubeConfigId
	 * @param addonId
	 * @return
	 * @throws IOException
	 */
	public boolean uninstallAddon(Long clusterIdx, String addonId) throws IOException {
		Long kubeConfigId = getKubeConfigId(clusterIdx);

		Addon addon = getAddons().get(addonId);
		log.info("Addon uninstall start. - KubeConfigId: {}", kubeConfigId);
		log.info("Addon uninstall start. - addonId: {}", addonId);
		log.info("Addon uninstall start. - addon name: {}", addon.getName());

		KubernetesClient client = new DefaultKubernetesClient();
		List<String> yamls = addon.getYamls();

		boolean result = true;
		for(int i=yamls.size() -1; i>=0; i--) {
			String yamlPath = yamls.get(i);
			
			ClassPathResource resource = new ClassPathResource(yamlPath);
			InputStream is = resource.getInputStream();

			List<HasMetadata> resoures = client.load(is).get();
			for (HasMetadata data : resoures) {
				String yamlString = Serialization.asYaml(data);

				YamlApplyParam param = YamlApplyParam.builder().kubeConfigId(kubeConfigId).yaml(yamlString).build();

				String kind = data.getKind();
				String name = data.getMetadata().getName();
				
				boolean isDelete = false;
				try {
					isDelete = commonProxy.delete(param);
					log.info("Addon uninstall - Success kind: {}, name: {}", kind, name);
				} catch (Exception e) {
					log.info("Addon uninstall - Fail kind: {}, name: {}", kind, name);
					result = false;
				}
			}
		}
		client.close();
		
		// DB 정보 삭제.
		addonDomainService.delete(clusterIdx, addonId);

		log.info("Addon uninstall. clusterIdx: {}, addonId: {}", clusterIdx, addonId);
		log.info("Addon uninstall result: {}", result);
		return result;
	}

	/**
	 * Addon 설치 여부 리턴.
	 * 
	 * @param clusterIdx
	 * @param addonType
	 * @return
	 */
	public boolean isInstall(Long clusterIdx, String addonType) {
		return addonDomainService.getEntityByType(clusterIdx, addonType) != null;
	}

	public Addon getAddonByType(Long clusterIdx, String addonType) {
		AddonEntity entity = addonDomainService.getEntityByType(clusterIdx, addonType);
		if (entity != null) {
			Addon addon = null;
			try {
				addon = getAddon(clusterIdx, entity.getAddonId());
			} catch (IOException e) {
				log.error("", e);
			}
			if(addon != null) {
				addon.setAddonEntity(entity);
			}
			return addon;
		}
		return null;
	}

	/**
	 * Cluster의 KubeConfigId 반환.
	 * 
	 * @param clusterIdx
	 * @return
	 */
	private Long getKubeConfigId(Long clusterIdx) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		return clusterEntity.getClusterId();
	}

	/**
	 * Addon 전체 리스트 반환.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, Addon> getAddons() throws IOException {
		if (addons == null) {
			addons = new HashMap<>();
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources(ADDON_PATH);
			Gson gson = new Gson();
			if (resources != null) {
				for (Resource r : resources) {
					String json = FileUtils.readLine(r.getInputStream());
					Addon addon = gson.fromJson(json, Addon.class);
					
					String iconPath = addon.getIconPath();
					Resource iconRes = resolver.getResource("classpath:" + iconPath);
					if(iconRes != null) {						
						byte[] iconBytes = ByteStreams.toByteArray(iconRes.getInputStream());
						String encodedString = Base64.getEncoder().encodeToString(iconBytes);						
						String extension = getFileExtension(iconPath);
						
						addon.setIconData(extension, encodedString);
					}
					addons.put(addon.getAddonId(), addon);
				}
			}
		}
		return addons;
	}
	
	

	/**
	 * Addon adapter 리턴.
	 * 
	 * @param addon
	 * @return
	 */
	private AddonAdapter getAdapter(Addon addon) {
		String a = addon.getAdapter();
		if (a != null) {
			try {
				Class<?> clazz = Class.forName(a);
				Constructor<?> ctor = clazz.getConstructor();
				AddonAdapter adapter = (AddonAdapter) ctor.newInstance();
				return adapter;
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return null;
	}

	public ServiceAdapterService getServiceAdapterService() {
		return serviceAdapterService;
	}

	public NodeAdapterService getNodeAdapterService() {
		return nodeAdapterService;
	}
	
	private String getFileExtension(String fileName) {
		if(fileName != null && fileName.contains(".")) {
			String extension = fileName.substring(
					fileName.lastIndexOf(".") + 1, 
					fileName.length());
			return extension.toLowerCase();
		}
		return null;
	}
}
