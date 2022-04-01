package kr.co.strato.domain.addon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.addon.model.AddonEntity;
import kr.co.strato.domain.addon.model.AddonIdPK;

public interface AddonRepository extends JpaRepository<AddonEntity, AddonIdPK> {
	
	/**
	 * Cluster Id에 설치되어 있는 Addon 목록 리턴.
	 * @param clusterIdx
	 * @return
	 */
	public List<AddonEntity> findByClusterIdx(Long clusterIdx);
	
	/**
	 * Cluster Id를 갖는 Addon 삭제.
	 * @param clusterIdx
	 */
	@Transactional
	public void deleteByClusterIdx(Long clusterIdx);
	
}
