package kr.co.strato.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectClusterEntity;

public interface ProjectClusterRepository extends JpaRepository<ProjectClusterEntity, Long>, ProjectClusterRepositoryCustom {

}
