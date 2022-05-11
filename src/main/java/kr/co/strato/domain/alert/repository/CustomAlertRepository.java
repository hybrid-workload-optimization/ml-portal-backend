package kr.co.strato.domain.alert.repository;

import java.util.List;

import kr.co.strato.domain.alert.model.AlertEntity;

public interface CustomAlertRepository {
	
	/**
	 * 최근 일주일 알람 내역
	 * @param userId
	 * @return
	 */
	public List<AlertEntity> getAlerts(String userId);
	
}
