package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;

public interface UserRoleMenuRepository extends JpaRepository<UserRoleMenuEntity, Long> {	
	public List<UserRoleMenuEntity> findByUserRole(UserRoleEntity userRole);
}
