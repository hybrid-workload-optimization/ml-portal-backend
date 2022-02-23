package kr.co.strato.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>, ProjectRepositoryCustom {
	
}