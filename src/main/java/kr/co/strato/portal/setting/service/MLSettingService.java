package kr.co.strato.portal.setting.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLSettingService {

	public String getCloudVender() {
		return "Azure";
	}
}
