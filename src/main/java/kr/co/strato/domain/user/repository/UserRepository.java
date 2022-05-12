package kr.co.strato.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.user.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>, CustomUserRepository {
	
	Optional<UserEntity> findByEmail(String email);
	
	Page<UserEntity> findByUserName(String userName, Pageable pageable);
	
	Optional<UserEntity> findByUserId(String userId);
	
	Optional<UserEntity> findByUserIdAndUseYn(String useId, String useYn);
	
	List<UserEntity> findByUseYn(String useYn);
	
	Page<UserEntity> findByUseYn(String useYn, Pageable pageable);
	
	@Transactional
	@Modifying
	@Query(value = "update user u set u.use_yn = ?1 where u.user_id = ?2", nativeQuery = true)
	int setUseYnByUser(String useYn, String userId);
}
