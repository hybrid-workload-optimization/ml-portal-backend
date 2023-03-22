package kr.co.strato.domain.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

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
@DynamicUpdate
@Table(name="project")
public class ProjectEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="project_idx")
	private Long id;
	
	@Column(name="uuid")
	private String uuid;
	
	@Column(name="project_name")
	private String projectName;
	
	private String description;
	
	@Column(name="create_user_id")
	private String createUserId;
	
	@Column(name="create_user_name")
	private String createUserName;
	
	@Column(name="created_at")
	private String createdAt;
	
	@Column(name="update_user_id")
	private String updateUserId;
	
	@Column(name="update_user_name")
	private String updateUserName;
	
	@Column(name="updated_at")
	private String updatedAt;
	
	@Column(name="deleted_yn")
	private String deletedYn;
}
