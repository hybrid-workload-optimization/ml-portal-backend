package kr.co.strato.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.user.model.UserResetPasswordEntity;

public interface UserResetPasswordRepository extends JpaRepository<UserResetPasswordEntity, String> {
	
	public UserResetPasswordEntity findByRequestCode(String requestCode);
	
	public UserResetPasswordEntity findByUserId(String userId);
	
	@Transactional
	public void deleteByUserId(String userId);
}
