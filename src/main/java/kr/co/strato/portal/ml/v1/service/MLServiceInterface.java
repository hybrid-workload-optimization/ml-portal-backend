package kr.co.strato.portal.ml.v1.service;

import io.fabric8.kubernetes.api.model.HasMetadata;

public interface MLServiceInterface {
	
	/**
	 * yaml 반영
	 * @param clusterIdx: 배포하려는 클러스터 아이디
	 * @param resourceId: 해당 리소스의 ID, 새로 생성하는 경우 null일 수 있음
	 * @param yaml
	 */
	public Long mlResourceApply(Long clusterIdx, Long resourceId, String yaml);
	
	/**
	 * 리소스 삭제
	 * @param resourceId
	 * @param yaml
	 * @return
	 */
	public boolean delete(Long resourceId, String yaml);
	
	/**
	 * 실제 리소스를 구해 리턴함.
	 * @param resourceId
	 * @return
	 */
	public Object getEntity(Long resourceId);
	
	
	/**
	 * 리소스 UID 리턴.
	 * @param resourceId
	 * @return
	 */
	public String getResourceUid(Long resourceId);
	
	
	/**
	 * 실제 리소스를 구해 리턴함.
	 * @param resourceId
	 * @return
	 */
	public HasMetadata getResource(Long resourceId);
	
}
