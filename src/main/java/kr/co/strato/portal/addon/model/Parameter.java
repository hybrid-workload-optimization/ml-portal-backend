package kr.co.strato.portal.addon.model;

import lombok.Data;

@Data
public class Parameter {
	private String label;
	private String name;	
	private String type;
	private boolean required;
	private Object defaultValue;
}
