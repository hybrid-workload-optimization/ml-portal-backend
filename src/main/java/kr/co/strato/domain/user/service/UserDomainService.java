package kr.co.strato.domain.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;

/**
 * @author tmdgh
 *
 */
@Service
public class UserDomainService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	/**
	 * 유저 등록/수정
	 * @param entity
	 */
	public void saveUser(UserEntity entity) {

		String roleCode = entity.getUserRole().getUserRoleCode();
		//권한 매핑 
		entity.getUserRole().setId(userRoleRepository.findTop1BByUserRoleCode(roleCode).getId());
		//DB 저장
		userRepository.save(entity);
	}
	
	
	
	/**
	 * 유저 삭제
	 * @param user
	 * @return
	 */
	public String deleteUser(UserEntity user) {
		
		//@TODO 유저 삭제에 대한 정책 확정 필요
		// useYn 플래그만 변경할지 ? 모든 정보 날린 뒤 useYn 플래그만 N으로 할지.

		Optional<UserEntity> entity =  userRepository.findById(user.getUserId());
		
		if(entity.isPresent()) {
			entity.get().setUseYn("N");
			userRepository.save(entity.get());
		}
		
		return user.getUserId();
	}
	
	
	
	/**
	 * 모든 유저 정보
	 * @return
	 */
	public Page<UserEntity> getAllUserList(Pageable pageable){
		System.out.println("========= UserDomainSErvice. getAllUSerList");
		System.out.println(pageable.toString());
		System.out.println(pageable.getSort());
		Page<UserEntity> list =  userRepository.findByUseYn("Y", pageable);
		if(list != null) System.out.println(list.toString());
		return list;
		
	}
	
	
	/**
	 * Email로 유저 정보 검색 > 단일
	 * @param email
	 * @return
	 */
	public UserEntity getUserInfoByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	
	/**
	 * UserId로 검색 > 단일
	 * @param userId
	 * @return
	 */
	public UserEntity getUserInfoByUserId(String userId) {
//		return userRepository.findByUserId(userId);
		// 사용중인 user만 검색하기 위해 useYn  추가
		return userRepository.findByUserIdAndUseYn(userId, "Y");
	}
	
	/**
	 * 이름으로 유저 정보 검색 > 다중
	 * @param userName
	 * @return
	 */
	public Page<UserEntity> getUserListByUserName(String userName, Pageable pageable){
		
		return userRepository.findByUserName(userName, pageable);
	}

	
	

}
