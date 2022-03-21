
package kr.co.strato.portal.setting.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCodeDto {

	//private static final long serialVersionUID = -3679525631942333310L;

	private String	groupCode;
	
	private String	groupName;
	
	private String	description;
	
	private	String 	useYn;

	private String createUserId;
	
	private String createUserName;
	
	private LocalDateTime createdAt;
	
	private List<CodeDto> codeList;

}
