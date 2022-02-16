package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmail(String email);
	
	List<User> findByUserName(String userName);
	
}
