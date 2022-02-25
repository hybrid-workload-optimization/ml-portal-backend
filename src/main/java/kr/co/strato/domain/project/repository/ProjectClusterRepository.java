package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectClusterEntity;

public interface ProjectClusterRepository extends JpaRepository<ProjectClusterEntity, Long>, ProjectClusterRepositoryCustom {

	public Integer deleteByProjectIdx(Long projectIdx);
	
	public List<ProjectClusterEntity> findByClusterIdxAndProjectIdxNot(Long clusterIdx, Long projectIdx);
}
