package kr.co.strato.domain.addon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.addon.model.AddonEntity;
import kr.co.strato.domain.addon.model.AddonIdPK;
import kr.co.strato.domain.addon.repository.AddonRepository;

@Service
public class AddonDomainService {

	@Autowired
	AddonRepository addonRepository;
	
	/**
	 * Addon 등록
	 * @param addonEntity
	 */
	public AddonEntity register(AddonEntity addonEntity) {
		return addonRepository.save(addonEntity);
	}
	
	/**
	 * Cluster ID에 설치된 Addon 목록 반환.
	 * @param clusterIdx
	 * @return
	 */
	public List<AddonEntity> getListByClusterId(Long clusterIdx) {
		return addonRepository.findByClusterIdx(clusterIdx);
	}
	
	/**
	 * Addon 반환
	 * @param clusterIdx
	 * @param addonId
	 * @return
	 */
	public AddonEntity getEntity(Long clusterIdx, String addonId) {
		AddonIdPK pk = new AddonIdPK();
		pk.setClusterIdx(clusterIdx);
		pk.setAddonId(addonId);
		
		Optional<AddonEntity> addon = addonRepository.findById(pk);
		if (addon.isPresent()) {
			return addon.get();
		}
		
		return null;
	}
	
	/**
	 * Addon 삭제
	 * @param clusterIdx
	 * @param addonId
	 */
	public void delete(Long clusterIdx, String addonId) {
		AddonIdPK pk = new AddonIdPK();
		pk.setClusterIdx(clusterIdx);
		pk.setAddonId(addonId);
		addonRepository.deleteById(pk);
	}
	
	/**
	 * Cluster ID에 
	 * @param clusterIdx
	 */
	public void deleteByCluseterId(Long clusterIdx) {
		addonRepository.deleteByClusterIdx(clusterIdx);
	}
	
	public AddonEntity getEntityByType(Long clusterIdx, String addonType) {
		Optional<AddonEntity> addon = addonRepository.findByClusterIdxAndAddonType(clusterIdx, addonType);
		if (addon.isPresent()) {
			return addon.get();
		}
		return null;
	}
}
