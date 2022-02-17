package kr.co.strato.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long>, CustomUserRoleRepository {

}
