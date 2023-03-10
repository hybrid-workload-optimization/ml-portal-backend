package kr.co.strato.adapter.sso.model;

import lombok.Data;

@Data
public class MessageModel {
	
	private String event;
	private String type;
	private Object data;
	private Object role;
	private Object group;
	private Object user;
	private String clientId;
	
}
