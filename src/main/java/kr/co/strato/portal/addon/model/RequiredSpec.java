package kr.co.strato.portal.addon.model;

import lombok.Data;

@Data
public class RequiredSpec {
	private String minKubeletVersion;	
	private String maxKubeletVersion;
	private String hwSpec;
}
