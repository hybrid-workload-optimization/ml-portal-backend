package kr.co.strato.adapter.k8s.kubespray.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.kubespray.model.KubesprayDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KubesprayAdapterService {
	@Autowired
	private KubesprayAdapterClient client;
	
	public List<String> getVersion() {
		log.debug("[KubesprayAdapterService.getVersion] start..");
		List<String> versions = client.getKubesprayVersion();
		log.debug("[KubesprayAdapterService.getVersion] end..");
		
		return versions;
	}
	
	public String getSetting(String version) {
		log.debug("[KubesprayAdapterService.getSetting] start..");
		String setting = client.getKubespraySetting(version);
		log.debug("[KubesprayAdapterService.getSetting] end..");
		
		return setting;
	}
	
	public boolean patchSetting(KubesprayDto kubesprayDto) {
		log.debug("[KubesprayAdapterService.patchSetting] start..");
		boolean flag = client.patchKubespraySetting(kubesprayDto);
		log.debug("[KubesprayAdapterService.patchSetting] end..");
		
		return flag;
	}
}
