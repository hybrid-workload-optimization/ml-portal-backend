package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>, CustomUserRepository {
	
	UserEntity findByEmail(String email);
	
	Page<UserEntity> findByUserName(String userName, Pageable pageable);
	
	UserEntity findByUserId(String userId);
	
	UserEntity findByUserIdAndUseYn(String useId, String useYn);
	
	public List<UserEntity> findByUseYn(String useYn);
	
	Page<UserEntity> findByUseYn(String useYn, Pageable pageable);
}
