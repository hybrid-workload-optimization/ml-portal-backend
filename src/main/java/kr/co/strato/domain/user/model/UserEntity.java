package kr.co.strato.domain.user.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name ="user")
public class UserEntity {
	
	@Id
	@Column(name = "user_id", unique = true)
	@NotNull
	private String userId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_password")
	private String userPassword;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "organization")
	private String organization;
	
	@Column(name = "contact")
	private String contact;
	
	@UpdateTimestamp
	@Column(name ="updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	@Column(name = "update_user_id")
	private String updateUserId;
	
	@Column(name = "update_user_name")
	private String updateUserName;
	
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "create_user_name")
	private String createUserName;
	
	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

//	@Column(name = "user_role_idx")
//	private Long userRoleIdx;
	
	@Column(name = "use_yn")
	private String useYn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_role_idx")
	private UserRoleEntity userRole;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name ="user_id")
	private List<ProjectUserEntity> projectUser;
	
}
