package kr.co.strato.adapter.k8s.kubespray.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
