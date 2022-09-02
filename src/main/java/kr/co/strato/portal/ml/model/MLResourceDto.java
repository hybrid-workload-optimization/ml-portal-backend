package kr.co.strato.portal.ml.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MLResourceDto {
	//리소스 이름
	private String name;
	
	//리소스 타입(Job, Deployment..)
	private String kind;
	
	//생성 시간
	private String createdAt;
	
	//수정 시간
	private String updatedAt;
	
	//리소스 uid
	private String uid;
	
	//리소스 상태
	private String status;
	
	//DB에서 관리되는 ID
	private Long resourceId;
	
	//yaml
	private String yaml;
}
