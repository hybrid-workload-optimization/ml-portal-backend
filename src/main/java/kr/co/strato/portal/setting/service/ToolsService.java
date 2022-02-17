package kr.co.strato.portal.setting.service;

import org.springframework.stereotype.Service;

import kr.co.strato.portal.setting.model.GeneralDto;

@Service
public class ToolsService {

	public Long patchTools(String type, GeneralDto params) {
		System.out.println("####type :: " + type);
		System.out.println("####paramse:: " + params.toString());
		return 0L;
	}

}
