package kr.co.strato.domain.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.code.model.CodeEntity;

public interface CodeRepository extends JpaRepository<CodeEntity, Long> {

	//public List<CodeEntity> findByGroupCode(String groupCode);
	
	public boolean existsByGroupCodeAndCommonCode(String groupCode, String commonCode);
		
}
