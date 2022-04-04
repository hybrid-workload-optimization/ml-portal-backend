package kr.co.strato.portal.addon.model;

import java.util.List;

import lombok.Data;

@Data
public class EndPoint {	
	private String name;
	private String uri;	
	private String type;	
	private Integer port;
	private String namespace;	
	private String serviceName;	
	private List<String> endpoints;
}
