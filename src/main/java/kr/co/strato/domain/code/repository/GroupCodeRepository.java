package kr.co.strato.domain.code.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.code.model.GroupCodeEntity;
import kr.co.strato.domain.user.model.UserEntity;

public interface GroupCodeRepository extends JpaRepository<GroupCodeEntity, String> {

	public List<GroupCodeEntity> findByUseYn(String useYn);
	
	//public GroupCodeEntity findByGroupCode(String groupCode);
	
}
