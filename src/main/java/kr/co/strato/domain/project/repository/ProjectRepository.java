package kr.co.strato.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>, ProjectRepositoryCustom {
	
	Optional<ProjectEntity>findByProjectNameAndDeletedYn(String projectName, String deletedYn);

	List<ProjectEntity> findByDeletedYn(String deletedYn);
}