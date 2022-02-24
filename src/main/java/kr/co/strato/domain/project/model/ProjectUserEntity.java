package kr.co.strato.domain.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@IdClass(ProjectUserPK.class)
@Table(name="project_user")
public class ProjectUserEntity {

	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(name="create_user_id")
	private String createUserId;
	
	@Column(name="create_user_name")
	private String createUserName;
	
	@Column(name="created_at")
	private String createdAt;
	
	@Column(name="project_user_role")
	private String projectUserRole;
	
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name="project_idx")
	//private ProjectEntity projectEntity;
	@Id
	@Column(name="project_idx")
	private Long projectIdx;
}