package kr.co.strato.portal.ml.service;

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
}
