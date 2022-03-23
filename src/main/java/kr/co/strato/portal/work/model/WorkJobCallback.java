package kr.co.strato.portal.work.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class WorkJobCallback<T> {

	private String result;
	
	private String message;
	
	private T data;
	
}
