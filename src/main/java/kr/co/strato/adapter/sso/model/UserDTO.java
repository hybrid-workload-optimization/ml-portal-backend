package kr.co.strato.adapter.sso.model;

//import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
    private String username;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String email;
//    private String createAt;
    private String createdBy;
//    private String updateAt;
    private String updateBy;
    private String contact;
    private String companyName;
    private Object roles;
    
}
