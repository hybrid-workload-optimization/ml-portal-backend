package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectClusterEntity;


public interface ProjectClusterRepository extends JpaRepository<ProjectClusterEntity, Long>, ProjectClusterRepositoryCustom {
    List<ProjectClusterEntity> findByProjectIdx(Long projectIdx);

	public Integer deleteByProjectIdx(Long projectIdx);
	
	public List<ProjectClusterEntity> findByClusterIdxAndProjectIdxNot(Long clusterIdx, Long projectIdx);
	
	public Integer deleteByProjectIdxAndClusterIdx(Long projectIdx, Long clusterIdx);
	
	public ProjectClusterEntity findByProjectIdxAndClusterIdx(Long projectIdx, Long clusterIdx);
	
	public Integer deleteByProjectIdxAndClusterIdxNotIn(Long projectIdx, List<Long> clusters);
	
	public List<ProjectClusterEntity> findByClusterIdx(Long clusterIdx);
}
