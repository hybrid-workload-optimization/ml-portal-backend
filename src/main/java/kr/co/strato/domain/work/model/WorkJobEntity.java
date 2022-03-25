package kr.co.strato.domain.work.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Table(name = "work_job")
public class WorkJobEntity {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_job_idx")
    private Long workJobIdx;
	
	@Column(name = "work_job_type")
	private String workJobType;
	
	@Column(name = "work_job_target")
	private String workJobTarget;
		
	@Column(name = "work_job_status")
	private String workJobStatus;
	
	@Column(name = "work_job_message")
	private String workJobMessage;
	
	@Lob
	@Column(name = "work_job_data_request")
	private String workJobDataRequest;
	
	@Lob
	@Column(name = "work_job_data_response")
	private String workJobDataResponse;
	
	@Column(name = "work_job_start_at")
	private String workJobStartAt;
	
	@Column(name = "work_job_end_at")
	private String workJobEndAt;
	
	@Column(name = "work_sync_yn")
	private String workSyncYn;
	
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "create_user_name")
	private String createUserName;
	
	@Column(name = "work_job_reference_idx")
	private Long workJobReferenceIdx;
	
}
