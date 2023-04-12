package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.model.ProjectUserEntity;

public interface ProjectUserRepository extends JpaRepository<ProjectUserEntity, String>, ProjectUserRepositoryCustom {

	public Integer deleteByProjectIdx(Long projectIdx);
	
	@Transactional
	public Integer deleteByProjectIdxAndUserId(Long projectIdx, String userId);
	
	public ProjectUserEntity findByProjectIdxAndUserId(Long projectIdx, String userId);
	
	public Integer deleteByProjectIdxAndUserIdNotIn(Long projectIdx, List<String> userIds);
	
	public List<ProjectUserEntity> findByUserId(String userId);
	
	public List<ProjectUserEntity> findByProjectIdx(Long projectIdx);
	
	public Integer deleteByProjectIdxAndUserRoleIdxNot(Long projectIdx, Long userRoleIdx);
}
