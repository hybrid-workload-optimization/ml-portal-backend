package kr.co.strato.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long>, CustomUserRoleRepository {
	
	public UserRoleEntity findTop1BByUserRoleName(String userRoleName);

	public UserRoleEntity findTop1BByUserRoleCode(String userRoleCode);
}
