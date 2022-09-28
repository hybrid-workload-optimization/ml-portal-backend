package kr.co.strato.domain.machineLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLProjectMappingEntity;

public interface MLProjectRepository extends JpaRepository<MLProjectMappingEntity, Long>, CustomMLProjectRepository {
	
	@Transactional
	public void deleteByMl(MLEntity ml);
}
