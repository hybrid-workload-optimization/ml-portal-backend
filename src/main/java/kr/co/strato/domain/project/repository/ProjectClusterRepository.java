package kr.co.strato.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectClusterEntity;

import java.util.List;

public interface ProjectClusterRepository extends JpaRepository<ProjectClusterEntity, Long>, ProjectClusterRepositoryCustom {
    List<ProjectClusterEntity> findByProjectIdx(Long projectIdx);
}
