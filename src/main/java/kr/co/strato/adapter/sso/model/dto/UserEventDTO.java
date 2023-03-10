package kr.co.strato.adapter.sso.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {
	
	private String uuid;
	private String userId;
    private String email;
    private String usernameEn;
    private String usernameKr;
	private Boolean enabled;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private String organization;
	private String department;
	private String employeeId;
    private String position;
    private String contact;
    private String rank;
    private String team;
    private String companyName;
    private Object picture;
    private Object roles;
    
}
