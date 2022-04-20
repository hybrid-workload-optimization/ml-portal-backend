package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.co.strato.domain.user.model.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long>, CustomUserRoleRepository {
	
	public UserRoleEntity findTop1BByUserRoleName(String userRoleName);

	public UserRoleEntity findTop1BByUserRoleCode(String userRoleCode);
	
	@Query(value = "SELECT COUNT(id) FROM UserRoleEntity WHERE userRoleName = ?1 AND groupYn = ?2")
	public int findCountByAccessRoleNameAndGroupYn(String userRoleName, String groupYn);
	
	
	public List<UserRoleEntity> findByUserRoleCodeNot(String userRoleCode);
	
}
