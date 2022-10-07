package kr.co.strato.adapter.cloud.common.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.cloud.aks.proxy.AKSInterfaceProxy;
import kr.co.strato.adapter.cloud.aks.service.AKSDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.proxy.InterfaceProxy;
import kr.co.strato.adapter.cloud.gke.service.GKSDefaultParamProvider;
import kr.co.strato.global.error.exception.BadRequestException;

@Service
public class CloudAdapterService {
	
	public static final String PROVIDER_AZURE = "Azure";
	public static final String PROVIDER_GCP = "GCP";
	public static final String PROVIDER_AWS = "AWS";
	
	@Autowired
	private AKSInterfaceProxy azureInterfaceProxy;

	/**
	 * 클러스터 프로비저닝
	 * @param provider
	 * @param obj
	 * @return
	 */
	public String provisioning(String provider, Map<String, Object> param) {	
		InterfaceProxy proxy = getInterfaceProxy(provider);
		if(proxy != null) {
			
			return proxy.provisioning(param);
		}
		throw new BadRequestException();
	}

	/**
	 * 클러스터 삭제
	 * @param provider
	 * @param clusterName
	 * @return
	 */
	public boolean delete(String provider, Map<String, Object> param) {
		InterfaceProxy proxy = getInterfaceProxy(provider);
		if(proxy != null) {
			return proxy.delete(param);
		}
		throw new BadRequestException();
	}

	/**
	 * 클러스터 스케일 조정
	 * @param provider
	 * @param arg
	 * @return
	 */
	public boolean scale(String provider, Map<String, Object> param) {
		InterfaceProxy proxy = getInterfaceProxy(provider);
		if(proxy != null) {
			return proxy.scale(param);
		}
		throw new BadRequestException();
	}
	
	

	/**
	 * 클라우드 벤더에 맞는 인터페이스 프록시 반환.
	 * @param provider
	 * @return
	 */
	public InterfaceProxy getInterfaceProxy(String provider) {
		InterfaceProxy proxy = null;
		if(provider.toLowerCase().equals(PROVIDER_AZURE.toLowerCase())) {
			proxy = azureInterfaceProxy;
		} else if(provider.toLowerCase().equals(PROVIDER_GCP.toLowerCase())) {
			
		} else if(provider.toLowerCase().equals(PROVIDER_AWS.toLowerCase())) {
			
		}
		return proxy;
	}
	
	public AbstractDefaultParamProvider getDefaultParamService(String provider) {
		AbstractDefaultParamProvider paramService = null;
		if(provider.toLowerCase().equals(PROVIDER_AZURE.toLowerCase())) {
			paramService = new AKSDefaultParamProvider();
		} else if(provider.toLowerCase().equals(PROVIDER_GCP.toLowerCase())) {
			paramService = new GKSDefaultParamProvider();
		} else if(provider.toLowerCase().equals(PROVIDER_AWS.toLowerCase())) {
			
		}
		return paramService;
	}
}
