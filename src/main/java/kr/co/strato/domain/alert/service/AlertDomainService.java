package kr.co.strato.domain.alert.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.alert.model.AlertEntity;
import kr.co.strato.domain.alert.repository.AlertRepository;

@Service
public class AlertDomainService {

	@Autowired
	AlertRepository alertRepository;
	
	public void register(AlertEntity entity) {
		alertRepository.save(entity);
	}
	
	public List<AlertEntity> getList(String userId) {
		return alertRepository.getAlerts(userId);
	}
	
	public boolean delete(Long alertIdx) {
		Optional<AlertEntity> alertEntity = alertRepository.findById(alertIdx);
		if(alertEntity.isPresent()) {
			alertRepository.deleteById(alertIdx);
			return true;
		}
		return false;
	}
	
	public boolean confirm(Long alertIdx) {
		return alertRepository.setComfirmById("Y", LocalDateTime.now(), alertIdx) > 0;
	}	
	
	public boolean is(String clusterName, String workJobType, String workJobStatus) {
		Optional<AlertEntity> alertEntity = alertRepository
				.findByClusterNameAndWorkJobTypeAndWorkJobStatus(clusterName, workJobType, workJobStatus);
		return alertEntity.isPresent();
	}
	
	public void deleteByUserId(String userId) {
		
	}
}
