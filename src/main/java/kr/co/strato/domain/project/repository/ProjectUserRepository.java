package kr.co.strato.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.project.model.ProjectUserEntity;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, String>, ProjectUserRepositoryCustom {

}