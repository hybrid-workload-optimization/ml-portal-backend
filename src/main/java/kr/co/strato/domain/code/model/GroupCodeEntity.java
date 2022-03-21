package kr.co.strato.domain.code.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "group_code")
public class GroupCodeEntity {

	@Id
	@NotNull
	@Column(name = "group_code")
	private String	groupCode;
	
	@Column(name = "group_name")
	private String	groupName;
	
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
	
	//@OneToMany(mappedBy = "groupCode", fetch = FetchType.LAZY )
	@OneToMany(cascade = CascadeType.DETACH)
	@JoinColumn(name="group_code", insertable=false, updatable=false)
	private List<CodeEntity> codeList;

}
