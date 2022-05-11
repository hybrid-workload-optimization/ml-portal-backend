package kr.co.strato.domain.alert.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.alert.model.AlertEntity;

public interface AlertRepository extends JpaRepository<AlertEntity, Long>, CustomAlertRepository {
	
	public List<AlertEntity> findByUserIdOrderByCreatedAtDesc(String userId);
	
	@Transactional
	@Modifying
	@Query(value = "update user_alert ua set ua.confirm_yn = ?1, ua.updated_at = ?2 where ua.alert_idx = ?3", nativeQuery = true)
	public int setComfirmById(String confirmYn, LocalDateTime time, Long alertIdx);
	
	
	public Optional<AlertEntity> findByClusterNameAndWorkJobTypeAndWorkJobStatus(String clusterName, String workJobType, String workJobStatus);
	
	@Transactional
	public void deleteByUserId(String userId);
	
}
