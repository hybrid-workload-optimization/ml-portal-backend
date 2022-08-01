package kr.co.strato.domain.machineLearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.machineLearning.model.MLResourceEntity;

public interface MLResourceRepository extends JpaRepository<MLResourceEntity, Long> {
	
	public List<MLResourceEntity> findByMlIdx(Long mlIdx);
	
	public Optional<MLResourceEntity> findByMlIdxAndMlResName(Long mlIdx, String mlResName);
	
	
	@Transactional
	public void deleteByMlIdx(Long mlIdx);
	
}
