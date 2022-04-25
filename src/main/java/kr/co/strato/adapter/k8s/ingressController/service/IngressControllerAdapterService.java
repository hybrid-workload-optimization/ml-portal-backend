package kr.co.strato.adapter.k8s.ingressController.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.ingressController.model.CreateIngressControllerParam;
import kr.co.strato.adapter.k8s.ingressController.model.IngressController;
import kr.co.strato.adapter.k8s.ingressController.proxy.IngressControllerProxy;

@Service
public class IngressControllerAdapterService {
	
	@Autowired
	private IngressControllerProxy ingressControllerProxy;
	
	/**
	 * IngressController 타입 리턴.
	 * @param provider
	 * @return
	 */
	public String[] types(String provider) {
		return ingressControllerProxy.types(provider);
	}

	/**
	 * IngressController 설치
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public String create(CreateIngressControllerParam param) throws IOException {
		String results = ingressControllerProxy.create(param);
		return results;
	}
	
	/**
	 * IngressController 수정
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public String update(CreateIngressControllerParam param) throws IOException {
		String results = ingressControllerProxy.update(param);
		return results;
	}
	
	/**
	 * IngressController 삭제
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public boolean remove(CreateIngressControllerParam param) {
		return ingressControllerProxy.remove(param);
	}
	
	/**
	 * IngressController 목록 리턴.
	 * @param kubeConfigId
	 * @return
	 */
	public List<IngressController> list(Long kubeConfigId) {
		return ingressControllerProxy.list(kubeConfigId);
	}
}
