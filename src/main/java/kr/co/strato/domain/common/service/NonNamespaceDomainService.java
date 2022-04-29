package kr.co.strato.domain.common.service;

public abstract class NonNamespaceDomainService {

	/**
	 * 중복되는 이름 여부 리턴
	 * @param clusterIdx
	 * @param namespaceIdx
	 * @param name
	 * @return
	 * 		true: 중복
	 * 		false: 중복 되지 않음
	 */
	public abstract boolean isDuplicateName(Long clusterIdx, String name);
	
}
