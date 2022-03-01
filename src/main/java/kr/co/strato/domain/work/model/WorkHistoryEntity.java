package kr.co.strato.domain.work.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "work_history")
public class WorkHistoryEntity {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_history_idx")
    private Long workHistoryIdx;
	
	@Column(name = "work_menu_1")
	private String workMenu1;
	
	@Column(name = "work_menu_2")
	private String workMenu2;
	
	@Column(name = "work_menu_3")
	private String workMenu3;
	
	@Column(name = "work_action")
	private String workAction;
	
	@Column
	private String target;
	
	@Column
	private String metadata;
	
	@Column
	private String result;
	
	@Column
	private String message;
	
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "create_user_name")
	private String createUserName;
	
	@Column(name = "created_at")
	private String createdAt;
	
	/*
	@Column(name = "work_job_idx")
	private Long workJobIdx;
	*/
	
}
