package kr.co.strato.domain.machineLearning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.machineLearning.model.MLEntity;

public interface MLRepository extends JpaRepository<MLEntity, Long>, CustomMLRepository {

	public Optional<MLEntity> findByMlId(String mlId);
	
	public Optional<MLEntity> findByClusterIdx(Long clusterIdx);
	
	@Transactional
	public void deleteByMlId(String mlId);
}
