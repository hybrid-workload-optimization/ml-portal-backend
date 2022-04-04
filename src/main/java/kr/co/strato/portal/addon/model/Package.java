package kr.co.strato.portal.addon.model;

import java.util.List;

import lombok.Data;

@Data
public class Package {
	private String name;
	private String desc;	
	private String version;
	private String image;
	private List<EndPoint> endpoints;	
}
