package kr.co.strato.domain.yaml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.yaml.model.YamlEntity;

public interface YamlRepository extends JpaRepository<YamlEntity, Long> {
	
	public List<YamlEntity> findByClusterIdxAndKindAndName(Long clusterIdx, String kind, String name);
	
	public List<YamlEntity> findByClusterIdxAndKindAndNameAndNamespace(Long clusterIdx, String kind, String name, String namespace);
	
	@Transactional
	public Integer deleteByClusterIdxAndKindAndName(Long clusterIdx, String kind, String name);
	
	@Transactional
	public Integer deleteByClusterIdxAndKindAndNameAndNamespace(Long clusterIdx, String kind, String name, String namespace);
	
	@Transactional
	public Integer deleteByClusterIdx(Long clusterIdx);
	
	
}
