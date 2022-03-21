package kr.co.strato.portal.setting.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeDto {
	
	//private static final long serialVersionUID = -7155392884990822761L;

	private Long codeIdx;
	
	private String	commonCode;

	private String	groupCode;
	
	private String	codeName;
	
	private String	codeValue;
	
	private Integer	codeOrder;

	private String	description;
	
	private	String 	useYn;

	private String createUserId;
	
	private String createUserName;
	
	private LocalDateTime createdAt;

	private String updateUserId;
	
	private String updateUserName;
	
	private LocalDateTime	updatedAt;

}
