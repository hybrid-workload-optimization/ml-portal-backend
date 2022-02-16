package kr.co.strato.domain.user.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

	private Long id;
	
	private String userName;
	
	private String email;
	
	private String organization;
	
	private String contact;
	
	private UserRole userRole; 
}
