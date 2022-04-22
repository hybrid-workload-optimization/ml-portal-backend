package kr.co.strato.domain.alert.model;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_alert")
public class AlertEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_idx", unique = true)
    private Long alertIdx;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "cluster_name")
	private String clusterName;
	
	@Column(name = "cluster_idx")
	private Long clusterIdx;
	
	@Column(name = "work_job_type")
	private String workJobType;
	
	@Column(name = "work_job_status")
	private String workJobStatus;
	
	@Column(name = "confirm_yn")
	private String confirmYn;
	
	@Column(name = "work_job_idx")
	private Long workJobIdx;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@Column(name = "updated_at")
	private String updatedAt;
}
