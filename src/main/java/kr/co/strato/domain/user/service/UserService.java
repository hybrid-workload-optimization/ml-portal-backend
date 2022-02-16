package kr.co.strato.domain.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.User;
import kr.co.strato.domain.user.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	/**
	 * 유저 등록
	 * @param user
	 * @return
	 */
	public Long register(User user) {
		
		userRepository.save(user);
		
		return user.getId();
	}
	
	
	/**
	 * 유저 삭제
	 * @param user
	 * @return
	 */
	public Long remove(User user) {
		userRepository.delete(user);
		
		return user.getId();
	}
	
	
	/**
	 * 모든 유저 정보
	 * @return
	 */
	public List<User> getAllUserList(){
		return userRepository.findAll();
	}
	
	
	/**
	 * Email로 유저 정보 검색 > 단일
	 * @param email
	 * @return
	 */
	public User	getUserInfoByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}
	
	/**
	 * 이름으로 유저 정보 검색
	 * @param userName
	 * @return
	 */
	public List<User> getUserListByUserName(String userName){
		
		return userRepository.findByUserName(userName);
	}
	
	
	

}
