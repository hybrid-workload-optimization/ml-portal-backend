package kr.co.strato.portal.common.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.domain.common.service.NonNamespaceDomainService;
import kr.co.strato.global.error.exception.DuplicateResourceNameException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NonNamespaceService extends CommonService {

	/**
	 * 생성하려는 리소스 이름 중복 채크
	 * 중복 발생시 DuplicateResourceNameException 발생
	 * Error code: 30001
	 * @param clusterIdx
	 * @param yaml
	 */
	public void duplicateCheckResourceCreation(Long clusterIdx, String yaml) {
		KubernetesClient client = getClient();
		
		//base64 decoding
		String decoded = base64Decoding(yaml);
		InputStream is = new ByteArrayInputStream(decoded.getBytes());
		List<HasMetadata> ress = client.load(is).get();
		
		for(HasMetadata data : ress) {
			String name = data.getMetadata().getName();
			
			NonNamespaceDomainService domainService = getDomainService();
			if(domainService != null) {
				boolean isDuplicate = domainService.isDuplicateName(clusterIdx, name);
				if(isDuplicate) {
					log.error("중복된 리소스 이름 입니다.");
					log.error("Resource name: {}", name);
					//리소스 중복 에러 발생.
					throw new DuplicateResourceNameException();
				}
			} else {
				log.error("중복 채크 에러. 도메인 서비스가 선언되어 있지 않습니다.");
			}
		}
	}
	
	/**
	 * namespace 도메인 서비스 반환.
	 * @return
	 */
	protected abstract NonNamespaceDomainService getDomainService();
	
}
