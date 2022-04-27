package kr.co.strato.domain.configMap.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.configMap.repository.ConfigMapRepository;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.repository.PersistentVolumeClaimRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class ConfigMapDomainService {

	@Autowired
	private ConfigMapRepository configMapRepository;
	
	public Long register(ConfigMapEntity configMapEntity) {
		configMapRepository.save(configMapEntity);
		
		return configMapEntity.getId();
	}
	
	public Page<ConfigMapEntity> getList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		return configMapRepository.getConfigMapList(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
	
	public ConfigMapEntity get(Long configMapIdx) {
		Optional<ConfigMapEntity> configMap = configMapRepository.findById(configMapIdx);
		if (configMap.isPresent()) {
			return configMap.get();
		} else {
			throw new NotFoundResourceException("Config Map : " + configMap.toString());
		}
	}
	
	public void delete(ConfigMapEntity configMapEntity) {
		configMapRepository.delete(configMapEntity);
	}
}
