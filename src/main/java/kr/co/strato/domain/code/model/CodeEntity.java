package kr.co.strato.domain.code.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "code")
public class CodeEntity {
	
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code_idx")
	private Long	codeIdx;

	@NotNull
	@Column(name = "common_code")
	private String	commonCode;

	@NotNull
	@Column(name = "group_code")
	private String	groupCode;
	
	@Column(name = "code_name")
	private String	codeName;
	
	@Column(name = "code_value")
	private String	codeValue;
	
	@Column(name = "code_order")
	private Integer	codeOrder;

	@Column(name = "description")
	private String	description;
	
	@Column(name = "use_yn", insertable=false, updatable=false)
	private	String 	useYn;

	@Column(name = "create_user_id", updatable=false)
	private String createUserId;
	
	@Column(name = "create_user_name", updatable=false)
	private String createUserName;
	
	@Column(name = "created_at", updatable=false)
	private LocalDateTime createdAt;

	@Column(name = "update_user_id")
	private String updateUserId;
	
	@Column(name = "update_user_name")
	private String updateUserName;
	
	@Column(name = "updated_at")
	private LocalDateTime	updatedAt; 	

//	@ManyToOne
//	@JoinColumn(name = "group_code")
//	private GroupCodeEntity groupCode;

}
