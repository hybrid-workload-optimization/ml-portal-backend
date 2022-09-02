package kr.co.strato.adapter.ml.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.adapter.ml.model.CloudParamDto.ScaleArg;
import kr.co.strato.adapter.ml.proxy.AzureInterfaceProxy;
import kr.co.strato.adapter.ml.proxy.InterfaceProxy;
import kr.co.strato.global.error.exception.BadRequestException;

@Service
public class CloudAdapterService {
	
	public static final String VENDER_AZURE = "Azure";
	public static final String VENDER_GCP = "GCP";
	public static final String VENDER_AWS = "AWS";
	
	@Autowired
	private AzureInterfaceProxy azureInterfaceProxy;

	/**
	 * 클러스터 프로비저닝
	 * @param vender
	 * @param obj
	 * @return
	 */
	public String provisioning(String vender, Object obj) {	
		InterfaceProxy proxy = getInterfaceProxy(vender);
		if(proxy != null) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(obj);
			
			Map map = gson.fromJson(json, Map.class);
			return proxy.provisioning(map);
		}
		throw new BadRequestException();
	}

	/**
	 * 클러스터 삭제
	 * @param vender
	 * @param clusterName
	 * @return
	 */
	public boolean delete(String vender, String clusterName) {
		InterfaceProxy proxy = getInterfaceProxy(vender);
		if(proxy != null) {
			return proxy.delete(clusterName);
		}
		throw new BadRequestException();
	}

	/**
	 * 클러스터 스케일 조정
	 * @param vender
	 * @param arg
	 * @return
	 */
	public boolean scale(String vender, ScaleArg arg) {
		InterfaceProxy proxy = getInterfaceProxy(vender);
		if(proxy != null) {
			return proxy.scale(arg);
		}
		throw new BadRequestException();
	}

	/**
	 * 클라우드 벤더에 맞는 인터페이스 프록시 반환.
	 * @param vender
	 * @return
	 */
	public InterfaceProxy getInterfaceProxy(String vender) {
		InterfaceProxy proxy = null;
		if(vender.toLowerCase().equals(VENDER_AZURE.toLowerCase())) {
			proxy = azureInterfaceProxy;
		} else if(vender.toLowerCase().equals(VENDER_GCP.toLowerCase())) {
			
		} else if(vender.toLowerCase().equals(VENDER_AWS.toLowerCase())) {
			
		}
		return proxy;
	}
}
