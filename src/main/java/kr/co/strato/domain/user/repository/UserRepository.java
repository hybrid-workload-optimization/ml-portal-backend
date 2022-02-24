package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
	
	UserEntity findByEmail(String email);
	
	Page<UserEntity> findByUserName(String userName, Pageable pageable);
	
	UserEntity findByUserId(String userId);
	
	public List<UserEntity> findByUseYn(String useYn);
}
