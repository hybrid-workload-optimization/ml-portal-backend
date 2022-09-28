package kr.co.strato.portal.setting.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLSettingService {
	
	public static String CLOUD_PROVIDER;

	/**
	 * 설정된 클라우드 공급자 반환.
	 * @return
	 */
	public String getCloudProvider() {
		if(CLOUD_PROVIDER == null) {
			CLOUD_PROVIDER = "Azure";
		}
		return CLOUD_PROVIDER;
	}
	
	/**
	 * 클라우드 공급자 설정.
	 * @param cloudProvider
	 */
	public void setCloudProvider(String cloudProvider) {
		CLOUD_PROVIDER = cloudProvider;
	}
}
