package kr.co.strato.portal.csp.vsphere.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class VSphereConfigurationService {

	/**
	 * vSphere의 Kubernetes 생성에 사용할 템플릿 리스트를 조회한다.
	 * @return
	 */
	public List<String> getTemplates(String cspAccountUuid) {		
		String[] arr = {"ubuntu-1804-kube-v1.24.11", "ubuntu-2004-kube-v1.25.7", "ubuntu-2004-kube-v1.26.2"};
		List<String> list = Arrays.asList(arr);
		return list;
	}
}
