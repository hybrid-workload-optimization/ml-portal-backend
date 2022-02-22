package kr.co.strato.domain.storageClass.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.storageClass.model.StorageClassEntity;

public interface StorageClassRepository extends JpaRepository<StorageClassEntity, Long>  {
	Page<StorageClassEntity> findByName(String name, Pageable pageable);// name 조회(Page 객체 반환)
	
	public Optional<StorageClassEntity> findByName(String name);
}
