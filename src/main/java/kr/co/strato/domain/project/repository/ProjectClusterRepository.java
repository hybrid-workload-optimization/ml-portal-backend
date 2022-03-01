package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectClusterEntity;

import java.util.List;


public interface ProjectClusterRepository extends JpaRepository<ProjectClusterEntity, Long>, ProjectClusterRepositoryCustom {
    List<ProjectClusterEntity> findByProjectIdx(Long projectIdx);

	public Integer deleteByProjectIdx(Long projectIdx);
	
	public List<ProjectClusterEntity> findByClusterIdxAndProjectIdxNot(Long clusterIdx, Long projectIdx);

}
