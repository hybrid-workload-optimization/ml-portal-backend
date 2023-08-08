package kr.co.strato.portal.ml.v1.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MLResourceDto {
	//ID
	private Long id;
	
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
	
	//파드 구동 상태
	private String pod;
	
	private int totalPodCount;
	
	private int runningPodCount;
}
